package com.kabryxis.spiritcraft.game.player;

import com.boydti.fawe.Fawe;
import com.kabryxis.kabutils.IndexingQueue;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.game.player.GamePlayer;
import com.kabryxis.kabutils.spigot.game.player.ResetFlag;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.kabutils.spigot.version.custom.player.WrappedInventory;
import com.kabryxis.kabutils.spigot.version.wrapper.entity.player.WrappedEntityPlayer;
import com.kabryxis.spiritcraft.game.DeadBody;
import com.kabryxis.spiritcraft.game.GhostParticleInfo;
import com.kabryxis.spiritcraft.game.ParticleTask;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownManager;
import com.kabryxis.spiritcraft.game.a.cooldown.ItemBarCooldown;
import com.kabryxis.spiritcraft.game.a.cooldown.PlayerCooldownManager;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.a.tracker.ItemTracker;
import com.kabryxis.spiritcraft.game.a.world.schematic.SchematicDataCreator;
import com.kabryxis.spiritcraft.game.inventory.DynamicInventory;
import com.kabryxis.spiritcraft.game.item.PlayerItemInfo;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpiritPlayer extends GamePlayer { // TODO combat logger
	
	private final SpiritGame game;
	private final PlayerItemInfo ghostItemInfo, hunterItemInfo;
	private final WrappedEntityPlayer entityPlayer;
	private final SchematicDataCreator schematicDataCreator;
	private final ItemTracker itemTracker;
	private final CooldownManager cooldownManager;
	private final DeadBody deadBody;
	private final ConfigSection customData;
	
	private Config data;
	private IndexingQueue<Block> fireWalkBlocks;
	
	private List<String> errorMessages = new ArrayList<>();
	
	private PlayerType playerType = PlayerType.WAITING;
	private boolean wantsGhost = true;
	private ParticleTask particleTask;
	private double damageToGhost = 0.0;
	private boolean breakBlocks = false;
	
	public SpiritPlayer(SpiritGame game, UUID uuid, Config data) {
		super(uuid);
		this.game = game;
		this.data = data;
		this.ghostItemInfo = new PlayerItemInfo(game.getItemManager().getItemData(true), this, true);
		this.hunterItemInfo = new PlayerItemInfo(game.getItemManager().getItemData(false), this, false);
		this.entityPlayer = WrappedEntityPlayer.newInstance();
		this.schematicDataCreator = new SchematicDataCreator(this);
		this.itemTracker = new ItemTracker(this);
		this.cooldownManager = new PlayerCooldownManager(abilityId -> new ItemBarCooldown(game, itemTracker.track("AbiId" + abilityId,
				item -> abilityId.equals(Items.getInt(item, "AbiId")))));
		this.deadBody = game.getDeadBodyManager().getDeadBody(this);
		this.customData = new ConfigSection();
		this.fireWalkBlocks = new IndexingQueue<>(12, block -> {
			game.getWorldManager().getBlockStateManager(block.getWorld()).revertState(block);
			game.getWorldManager().getMetadataProvider().removeMetadata(block, "nospread");
		});
		this.fireWalkBlocks.setIndexAction(5, block -> {
			game.getWorldManager().getBlockStateManager(block.getWorld()).createState(block);
			game.getWorldManager().getMetadataProvider().addEmptyMetadata(block, "nospread");
			block.setType(Material.FIRE);
		});
	}
	
	public SpiritGame getGame() {
		return game;
	}
	
	public Config getData() {
		return data;
	}
	
	@Override
	public void updatePlayer(Player player) {
		super.updatePlayer(player);
		entityPlayer.setHandle(player);
		WrappedInventory.wrap(player, itemTracker);
		itemTracker.updateReferences();
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
		return Fawe.imp().wrap(player).getSelection();
	}
	
	public SchematicDataCreator getDataCreator() {
		return schematicDataCreator;
	}
	
	public ItemTracker getItemTracker() {
		return itemTracker;
	}
	
	public CooldownManager getCooldownManager() {
		return cooldownManager;
	}
	
	public DeadBody getDeadBody() {
		return deadBody;
	}
	
	public ConfigSection getCustomData() {
		return customData;
	}
	
	public IndexingQueue<Block> getFireWalkBlocks() {
		return fireWalkBlocks;
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
		data.put("currency", currency);
		data.save();
	}
	
	public int getCurrency() {
		return data.computeIntIfAbsent("currency", 100);
	}
	
	public void setItemSpace(int space) {
		data.put("space", space);
		data.save();
	}
	
	public int getItemSpace() {
		return data.computeIntIfAbsent("space", 100);
	}
	
	public void setParticleEffect(String particleName) {
		data.put("particle-effect", particleName);
		data.save();
	}
	
	public String getParticleEffect() {
		return data.computeIfAbsent("particle-effect", "flame");
	}
	
	public GhostParticleInfo getParticleData() {
		return game.getParticleManager().getParticleData(getParticleEffect());
	}
	
	public boolean wantsGhost() {
		return wantsGhost;
	}
	
	public boolean isInGame() {
		return playerType == PlayerType.GHOST || playerType == PlayerType.HUNTER;
	}
	
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
			particleTask = null;
		}
		super.reset(flags);
		itemTracker.untrackAll();
		// TODO more that needs to be reset
		playerType = PlayerType.WAITING;
	}
	
	public BlockFace getFacingDirection() {
		float yaw = player.getLocation().getYaw();
		if(yaw < 0.0F) yaw += 180.0F;
		if(yaw >= 45.0F && yaw <= 135.0F) return BlockFace.EAST;
		if(yaw >= 135.0F && yaw <= 225.0F) return BlockFace.SOUTH;
		if(yaw >= 225.0F && yaw <= 315.0F) return BlockFace.WEST;
		return BlockFace.NORTH;
	}
	
	public int getPing() {
		return entityPlayer.getPing();
	}
	
	public boolean canBreakBlocks() {
		return breakBlocks;
	}
	
	public void toggleBreakBlocks() {
		breakBlocks = !breakBlocks;
	}
	
}
