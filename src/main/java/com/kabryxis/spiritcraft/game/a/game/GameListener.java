package com.kabryxis.spiritcraft.game.a.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import com.kabryxis.kabutils.IndexingQueue;
import com.kabryxis.kabutils.data.Maps;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.kabutils.spigot.listener.Listeners;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownEntry;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownHandler;
import com.kabryxis.spiritcraft.game.object.TriggerType;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Openable;
import org.bukkit.util.Vector;
import org.inventivetalent.particle.ParticleEffect;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GameListener implements Listener {
	
	private final Set<Entity> lastPortalTimestamp = Sets.newSetFromMap(Maps.getFromCache(CacheBuilder.newBuilder().expireAfterWrite(2500, TimeUnit.MILLISECONDS)));
	private final Set<Player> lastInteractTimestamp = Sets.newSetFromMap(Maps.getFromCache(CacheBuilder.newBuilder().expireAfterWrite(100, TimeUnit.MILLISECONDS)));
	private final BlockFace[] nearbyFaces = { BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH };
	
	private final SpiritGame game;
	
	public GameListener(SpiritGame game) {
		this.game = game;
		Listeners.cancelEvents(game.getPlugin(), game::isInProgress, BlockBurnEvent.class);
	}
	
	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		EntityRegainHealthEvent.RegainReason reason = event.getRegainReason();
		if(reason != EntityRegainHealthEvent.RegainReason.CUSTOM && reason != EntityRegainHealthEvent.RegainReason.MAGIC
				&& reason != EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		if(!(entity instanceof Player)) {
			event.setCancelled(true);
			return;
		}
		SpiritPlayer defender = game.getPlayerManager().getPlayer((Player)entity);
		SpiritPlayer attacker;
		Entity damager = event.getDamager();
		if(damager instanceof Player) attacker = game.getPlayerManager().getPlayer((Player)event.getDamager());
		else if(damager instanceof Projectile) attacker = game.getPlayerManager().getPlayer((Player)((Projectile)damager).getShooter());
		else return;
		if(!event.isCancelled()) attacker.addDamageToGhost(event.getDamage()); // TODO seperate event with monitor priority
		if(defender.getPlayerType() != PlayerType.GHOST) return;
		Location loc = defender.getLocation().add(0, 0.75, 0);
		ParticleEffect.BLOCK_DUST.sendData(defender.getPlayer().getWorld().getPlayers(), loc.getX(), loc.getY(), loc.getZ(),
				0.2, 0.66, 0.2, 0, 50, new ItemStack(Material.REDSTONE_BLOCK));
		defender.getPlayer().getWorld().playSound(loc, Sound.ZOMBIE_HURT, 1F, 0.1F); // TODO call by soundmanager
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getCause() == EntityDamageEvent.DamageCause.FALL &&
				game.getPlayerManager().getPlayer((Player)event.getEntity()).getPlayerType() == PlayerType.GHOST) event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityPortalEnter(EntityPortalEnterEvent event) {
		Entity entity = event.getEntity();
		if(lastPortalTimestamp.add(entity)) {
			// TODO
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		SpiritPlayer player = game.getPlayerManager().getPlayer(event.getPlayer());
		Action action = event.getAction();
		ItemStack item = event.getItem();
		if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR && Items.exists(item))  {
			// right clicking with an item in hand causes the item instance to change for whatever reason
			player.getItemTracker().untrack(item);
			int slot = player.getInventory().getHeldItemSlot();
			game.getTaskManager().start(() -> player.getItemTracker().track(player.getInventory().getItem(slot)));
		}
		if(action != Action.PHYSICAL) {
			if(!lastInteractTimestamp.add(player.getPlayer())) return;
			Block block = event.getClickedBlock();
			if(action == Action.RIGHT_CLICK_BLOCK && (block.getState() instanceof InventoryHolder ||
					block.getState().getData() instanceof Openable)) return; // TODO
			ConfigSection triggerData = new ConfigSection();
			CooldownHandler cooldownHandler = new CooldownHandler(player.getCooldownManager());
			triggerData.put("cooldownHandler", cooldownHandler);
			triggerData.put("triggerer", player);
			triggerData.put("target", player);
			triggerData.put("type", action == Action.LEFT_CLICK_BLOCK ||
					action == Action.LEFT_CLICK_AIR ? TriggerType.LEFT_CLICK : TriggerType.RIGHT_CLICK);
			triggerData.put("hand", item);
			triggerData.put("block", block);
			triggerData.put("abilityId", Items.getInt(item, "AbiId"));
			int cooldown = Items.getInt(item, "cooldown");
			if(cooldown > 0) cooldownHandler.setCooldown(new CooldownEntry(cooldown, triggerData));
			game.getAbilityManager().perform(triggerData);
			game.getObjectiveManager().perform(triggerData);
			if(triggerData.getBoolean("cancel", true)) event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		SpiritPlayer player = game.getPlayerManager().getPlayer(event.getPlayer());
		if(player.getPlayerType() == PlayerType.GHOST) player.getParticleTask().setSkipTick(event.isSneaking());
	}
	
	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) {
		if(event.getSource().hasMetadata("nospread")) {
			event.setCancelled(true);
			return;
		}
		Block bseBlock = event.getBlock();
		boolean shouldSpread = false;
		for(BlockFace face : nearbyFaces) {
			Block relative = bseBlock.getRelative(face);
			Material relativeType = relative.getType();
			if(relativeType == Material.CARPET || relativeType == Material.WALL_SIGN || relativeType == Material.SIGN_POST)
				relative.setType(Material.FIRE);
			else if(relativeType.isSolid()) shouldSpread = true;
		}
		if(!shouldSpread) event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockFade(BlockFadeEvent event) {
		Block bfeBlock = event.getNewState().getBlock();
		for(BlockFace face : nearbyFaces) {
			if(bfeBlock.getRelative(face).getType().isSolid()) {
				event.setCancelled(true);
				break;
			}
		}
	}
	
	@EventHandler
	public void onBlockCanBuild(BlockCanBuildEvent event) {
		Material type = event.getBlock().getType();
		if(type == Material.CARPET || type == Material.WALL_SIGN || type == Material.SIGN_POST || type == Material.FLOWER_POT ||
				type == Material.FLOWER_POT_ITEM) event.setBuildable(true);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player bukkitPlayer = event.getPlayer();
		SpiritPlayer player = game.getPlayerManager().getPlayer(bukkitPlayer);
		/*game.forEachPlayer(p -> {
			if(bukkitPlayer != p.getPlayer()) bukkitPlayer.sendMessage(String.format("LOS of %s: %s", p.getName(), bukkitPlayer.hasLineOfSight(p.getPlayer())));
		});*/
		if(player.getPlayerType() == PlayerType.GHOST) {
			if(hasMoved(event.getFrom(), event.getTo())) {
				game.forEachHunter(hunter -> {
					if(crosshairNears(hunter, player)) player.sendMessage("%s is looking at you.", hunter.getName());
				});
			}
		}
		else if(player.getPlayerType() == PlayerType.HUNTER) {
			if(hasTurned(event.getFrom(), event.getTo())) {
				game.forEachGhost(ghost -> {
					if(crosshairNears(player, ghost)) ghost.sendMessage("%s is looking at you.", player.getName());
				});
			}
		}
		if(bukkitPlayer.isSneaking()) return;
		Block block = event.getFrom().getBlock();
		Material belowType = block.getRelative(BlockFace.DOWN).getType();
		if(belowType == Material.AIR || belowType == Material.FIRE) return;
		Material type = block.getType();
		if(type != Material.AIR && type != Material.CARPET) {
			block = block.getRelative(BlockFace.UP);
			if(block.getType() != Material.AIR) return;
		}
		IndexingQueue<Block> fireWalkBlocks = player.getFireWalkBlocks();
		if(player.getCustomData().getBoolean("firewalk", false) && !fireWalkBlocks.contains(block)) fireWalkBlocks.add(block);
	}
	
	private boolean crosshairNears(Player player, Player target) {
		if(!player.hasLineOfSight(target)) return false;
		Vector direction = player.getLocation().getDirection();
		Location startLoc = player.getEyeLocation(), targetLoc = target.getLocation();
		double lastDist = Double.MAX_VALUE;
		while(!startLoc.getBlock().getType().isSolid()) {
			double currLastDist = startLoc.distanceSquared(targetLoc);
			if(currLastDist <= 4.0) return true;
			if(currLastDist <= lastDist) lastDist = currLastDist;
			else return false;
			startLoc.add(direction);
		}
		return false;
	}
	
	private boolean hasMoved(Location from, Location to) {
		return from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();
	}
	
	private boolean hasTurned(Location from, Location to) {
		return from.getYaw() != to.getYaw() || from.getPitch() != to.getPitch();
	}
	
}
