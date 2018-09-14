package com.kabryxis.spiritcraft.game.player;

import com.boydti.fawe.object.FawePlayer;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.spigot.game.player.GamePlayer;
import com.kabryxis.kabutils.spigot.game.player.ResetFlag;
import com.kabryxis.kabutils.spigot.version.wrapper.entity.player.WrappedEntityPlayer;
import com.kabryxis.spiritcraft.game.ParticleData;
import com.kabryxis.spiritcraft.game.ParticleTask;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.a.world.schematic.SchematicDataCreator;
import com.kabryxis.spiritcraft.game.inventory.DynamicInventory;
import com.kabryxis.spiritcraft.game.item.PlayerItemInfo;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpiritPlayer extends GamePlayer {
	
	private final Game game;
	private final Config data;
	private final PlayerItemInfo ghostItemInfo, hunterItemInfo;
	private final WrappedEntityPlayer entityPlayer;
	
	private SchematicDataCreator creator;
	
	private List<String> errorMessages = new ArrayList<>();
	
	private PlayerType playerType = PlayerType.WAITING;
	private boolean wantsGhost = true;
	private ParticleTask particleTask;
	private double damageToGhost = 0.0;
	
	public SpiritPlayer(Game game, UUID uuid, Config data) {
		super(uuid);
		this.game = game;
		this.data = data;
		this.ghostItemInfo = new PlayerItemInfo(game.getItemManager().getItemData(true), this, true);
		this.hunterItemInfo = new PlayerItemInfo(game.getItemManager().getItemData(false), this, false);
		this.entityPlayer = WrappedEntityPlayer.newInstance();
		data.load();
	}
	
	public Game getGame() {
		return game;
	}
	
	public Config getData() {
		return data;
	}
	
	@Override
	public void updatePlayer(Player player) {
		this.player = player;
		entityPlayer.setPlayer(player);
	}
	
	public WrappedEntityPlayer getEntityPlayer() {
		return entityPlayer;
	}
	
	public PlayerItemInfo getGhostItemInfo() {
		return ghostItemInfo;
	}
	
	public PlayerItemInfo getHunterItemInfo() {
		return hunterItemInfo;
	}
	
	public PlayerItemInfo getPlayerItemInfo(boolean ghost) {
		return ghost ? ghostItemInfo : hunterItemInfo;
	}
	
	public Region getSelection() {
		return FawePlayer.wrap(player).getSelection();
	}
	
	public SchematicDataCreator getCreator() {
		return creator == null ? (creator = new SchematicDataCreator(this)) : creator;
	}
	
	public boolean hasErrorMessages() {
		return !errorMessages.isEmpty();
	}
	
	public void addErrorMessage(String message) {
		errorMessages.add(message);
	}
	
	public List<String> getLastErrorMessages(int amount) {
		List<String> msgs = new ArrayList<>(amount);
		for(int i = Math.max(errorMessages.size() - amount, 0); i < errorMessages.size(); i++) {
			msgs.add(errorMessages.get(i));
		}
		return msgs;
	}
	
	public void clearLastErrorMessages(int amount) {
		int end = Math.max(errorMessages.size() - amount, 0);
		for(int i = errorMessages.size() - 1; i >= end; i--) {
			errorMessages.remove(i);
		}
	}
	
	public void clearAllErrorMessages() {
		errorMessages.clear();
	}
	
	public void setCurrency(int currency) {
		data.set("currency", currency);
		data.save();
	}
	
	public int getCurrency() {
		return data.get("currency", Integer.class, 100, true);
	}
	
	public void setItemSpace(int space) {
		data.set("space", space);
		data.save();
	}
	
	public int getItemSpace() {
		return data.get("space", Integer.class, 100, true);
	}
	
	public void setParticleEffect(String particleName) {
		data.set("particle-effect", particleName);
		data.save();
	}
	
	public String getParticleEffect() {
		return data.get("particle-effect", String.class, "flame", true);
	}
	
	public ParticleData getParticleData() {
		return game.getParticleManager().getParticleData(getParticleEffect());
	}
	
	public boolean wantsGhost() {
		return wantsGhost;
	}
	
	public boolean isInGame() {
		return playerType == PlayerType.GHOST || playerType == PlayerType.HUNTER;
	}
	
	/*public boolean isInSpiritDim() {
		return game.getCurrentArenaData().getSpiritDimData().getDimInfo().getLocation().getWorld().getName().equals(getLocation().getWorld().getName());
	}*/
	
	public void setPlayerType(PlayerType playerType) {
		this.playerType = playerType;
	}
	
	public PlayerType getPlayerType() {
		return playerType;
	}
	
	public void openInventory(DynamicInventory inventory) {
		inventory.open(player);
	}
	
	public void startParticleTimer() {
		if(particleTask != null) particleTask.cancel();
		particleTask = new ParticleTask(this);
		particleTask.start();
	}
	
	public ParticleTask getParticleTask() {
		return particleTask;
	}
	
	public void addDamageToGhost(double damage) {
		damageToGhost += damage;
	}
	
	public void resetDamageToGhost() {
		damageToGhost = 0.0;
	}
	
	public double getDamageToGhost() {
		return damageToGhost;
	}
	
	@Override
	public void reset(ResetFlag... flags) {
		if(particleTask != null) {
			particleTask.cancel();
			particleTask = null; // TODO find better way to handle tasks rather than creating new instances
		}
		super.reset(flags);
		playerType = PlayerType.WAITING;
	}
	
	public BlockFace getFacingDirection() {
		float yaw = player.getLocation().getYaw();
		if(yaw < 0.0F) yaw += 180.0F;
		if(yaw >= 45.0F && yaw <= 135.0F) return BlockFace.EAST;
		if(yaw >= 135.0F && yaw <= 225.0F) return BlockFace.SOUTH;
		if(yaw >= 225.0F && yaw <= 315.0F) return BlockFace.WEST;
		else return BlockFace.NORTH;
	}
	
	public int getPing() {
		return entityPlayer.getPing();
	}
	
	/*public DimData getCurrentDimData() {
		String playerWorldName = player.getWorld().getName();
		ArenaData arenaData = game.getCurrentArenaData();
		if(playerWorldName.equals(arenaData.getNormalDimData().getDimInfo().getLocation().getWorld().getName())) return arenaData.getNormalDimData();
		else if(playerWorldName.equals(arenaData.getSpiritDimData().getDimInfo().getLocation().getWorld().getName())) return arenaData.getSpiritDimData();
		else {
			sendMessage("where the hell are you? (cannot find you in normal or spirit dimension)");
			return null; // TODO maybe throw error
		}
	}*/
	
}
