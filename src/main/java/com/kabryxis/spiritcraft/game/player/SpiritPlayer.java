package com.kabryxis.spiritcraft.game.player;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.spigot.game.player.GamePlayer;
import com.kabryxis.kabutils.spigot.game.player.ResetFlag;
import com.kabryxis.kabutils.spigot.version.wrapper.entity.player.WrappedEntityPlayer;
import com.kabryxis.kabutils.spigot.world.schematic.BlockSelection;
import com.kabryxis.spiritcraft.game.ParticleData;
import com.kabryxis.spiritcraft.game.ParticleTask;
import com.kabryxis.spiritcraft.game.SchematicCreator;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.ability.StabChargeTask;
import com.kabryxis.spiritcraft.game.inv.DynamicInventory;
import com.kabryxis.spiritcraft.game.inv.PlayerItemInfo;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpiritPlayer extends GamePlayer {
	
	private final Game game;
	private final Config data;
	private final PlayerItemInfo ghostItemInfo, hunterItemInfo;
	private final WrappedEntityPlayer entityPlayer;
	
	private BlockSelection selection;
	private SchematicCreator creator;
	
	private DynamicInventory current, previous;
	private List<String> errorMessages = new ArrayList<>();
	
	private PlayerType playerType = PlayerType.WAITING;
	private boolean wantsGhost = true;
	private ParticleTask particleTask;
	private BukkitTask finisherTask;
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
	
	public BlockSelection getSelection() {
		return selection == null ? (selection = new BlockSelection(player)) : selection;
	}
	
	public SchematicCreator getCreator() {
		return creator == null ? (creator = new SchematicCreator(this)) : creator;
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
	
	public boolean isPlaying() {
		return playerType != PlayerType.SPECTATOR;
	}
	
	public boolean isInGame() {
		return playerType == PlayerType.GHOST || playerType == PlayerType.HUNTER;
	}
	
	public boolean isInSpiritDim() {
		return game.getCurrentArenaData().getSpiritDimData().getDimInfo().getLocation().getWorld().getName().equals(getLocation().getWorld().getName());
	}
	
	public void setPlayerType(PlayerType playerType) {
		this.playerType = playerType;
	}
	
	public PlayerType getPlayerType() {
		return playerType;
	}
	
	public void openInventory(DynamicInventory inventory) {
		this.current = inventory;
		inventory.open(this);
	}
	
	public void archiveInventory() {
		this.previous = current;
		this.current = null;
	}
	
	public DynamicInventory getCurrent() {
		return current;
	}
	
	public DynamicInventory getPrevious() {
		return previous;
	}
	
	public boolean hasPrevious() {
		return previous != null;
	}
	
	public void startParticleTimer() {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20000000, 0, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20000000, 0, false, false));
		if(particleTask == null) particleTask = new ParticleTask(this);
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
		if(particleTask != null) particleTask.cancel();
		resetCharge();
		super.reset(flags);
		playerType = PlayerType.WAITING;
	}
	
	public void startCharge(ItemStack item) {
		if(finisherTask == null) finisherTask = new StabChargeTask(this, item).start();
	}
	
	public void resetCharge() {
		if(finisherTask != null) {
			finisherTask.cancel();
			finisherTask = null;
		}
	}
	
	public BlockFace getFacingDirection() {
		float yaw = player.getLocation().getYaw();
		if(yaw < 0.0F) yaw += 180.0F;
		if(yaw >= 45.0F && yaw <= 135.0F) return BlockFace.EAST;
		if(yaw >= 135.0F && yaw <= 225.0F) return BlockFace.SOUTH;
		if(yaw >= 225.0F && yaw <= 315.0F) return BlockFace.WEST;
		else return BlockFace.NORTH;
	}
	
}
