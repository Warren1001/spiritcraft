package com.kabryxis.spiritcraft.game.a.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import com.kabryxis.kabutils.data.Maps;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.kabutils.spigot.listener.GlobalListener;
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
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Openable;
import org.inventivetalent.particle.ParticleEffect;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GameListener implements GlobalListener {
	
	private final Set<Entity> lastPortalTimestamp = Sets.newSetFromMap(Maps.getFromCache(CacheBuilder.newBuilder().expireAfterWrite(2500, TimeUnit.MILLISECONDS)));
	private final Set<Player> lastInteractTimestamp = Sets.newSetFromMap(Maps.getFromCache(CacheBuilder.newBuilder().expireAfterWrite(100, TimeUnit.MILLISECONDS)));
	private final BlockFace[] nearbyFaces = { BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH };
	
	private final SpiritGame game;
	
	public GameListener(SpiritGame game) {
		this.game = game;
	}
	
	@Override
	public void onEvent(Event event) { // TODO hop off of switch statement, gonna get laggy eventually. Listeners#callEvent(Listener, Event)
		switch(event.getEventName()) {
			case "EntityDamageByEntityEvent":
				EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)event;
				if(!(edbee.getEntity() instanceof Player)) {
					edbee.setCancelled(true);
					break;
				}
				SpiritPlayer defender = game.getPlayerManager().getPlayer((Player)edbee.getEntity());
				SpiritPlayer attacker;
				Entity damager = edbee.getDamager();
				if(damager instanceof Player) attacker = game.getPlayerManager().getPlayer((Player)edbee.getDamager());
				else if(damager instanceof Projectile) attacker = game.getPlayerManager().getPlayer((Player)((Projectile)damager).getShooter());
				else break;
				if(!edbee.isCancelled()) attacker.addDamageToGhost(edbee.getDamage()); // TODO seperate event with monitor priority
				if(defender.getPlayerType() != PlayerType.GHOST) break;
				Location loc = defender.getLocation().add(0, 0.75, 0);
				ParticleEffect.BLOCK_DUST.sendData(defender.getPlayer().getWorld().getPlayers(), loc.getX(), loc.getY(), loc.getZ(),
						0.2, 0.66, 0.2, 0, 50, new ItemStack(Material.REDSTONE_BLOCK));
				defender.getPlayer().getWorld().playSound(loc, Sound.ZOMBIE_HURT, 1F, 0.1F); // TODO call by soundmanager
				break;
			case "EntityDamageEvent":
				EntityDamageEvent ede = (EntityDamageEvent)event;
				Entity edeEntity = ede.getEntity();
				if(!(edeEntity instanceof Player) || (ede.getCause() == EntityDamageEvent.DamageCause.FALL &&
						game.getPlayerManager().getPlayer((Player)edeEntity).getPlayerType() == PlayerType.GHOST)) ede.setCancelled(true);
				break;
			case "EntityPortalEnterEvent":
				EntityPortalEnterEvent epee = (EntityPortalEnterEvent)event;
				Entity epeeEntity = epee.getEntity();
				if(lastPortalTimestamp.add(epeeEntity)) {
					// TODO
				}
				break;
			case "PlayerInteractEvent":
				PlayerInteractEvent pie = (PlayerInteractEvent)event;
				SpiritPlayer player = game.getPlayerManager().getPlayer(pie.getPlayer());
				Action action = pie.getAction();
				ItemStack item = pie.getItem();
				if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR && Items.exists(item))  {
					// right clicking with an item in hand causes the item instance to change for whatever reason
					player.getItemTracker().untrack(item);
					int slot = player.getInventory().getHeldItemSlot();
					game.getTaskManager().start(() -> player.getItemTracker().track(player.getInventory().getItem(slot)));
				}
				if(action != Action.PHYSICAL) {
					if(!lastInteractTimestamp.add(player.getPlayer())) break;
					Block block = pie.getClickedBlock();
					if(action == Action.RIGHT_CLICK_BLOCK && (block.getState() instanceof InventoryHolder ||
							block.getState().getData() instanceof Openable)) break; // TODO
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
					if(triggerData.getBoolean("cancel", true)) pie.setCancelled(true);
				}
				break;
			/*case "PlayerChangedDimEvent":
				PlayerChangedDimEvent pcde = (PlayerChangedDimEvent)event;
				SpiritPlayer pcdePlayer = pcde.getPlayer();
				if(pcdePlayer.getPlayerType() == PlayerType.GHOST) {
					ParticleTask particleTask = pcdePlayer.getParticleTask();
					if(pcde.getNewDim() == PlayerChangedDimEvent.DimType.NORMAL) particleTask.setDefaultDelay();
					else particleTask.setDelay(particleTask.getDelay() / 8);
				}
				break;*/
			case "PlayerToggleSneakEvent":
				PlayerToggleSneakEvent ptse = (PlayerToggleSneakEvent)event;
				game.getPlayerManager().getPlayer(ptse.getPlayer()).getParticleTask().setSkipTick(ptse.isSneaking());
				break;
			case "BlockSpreadEvent":
				BlockSpreadEvent bse = (BlockSpreadEvent)event;
				Block bseBlock = bse.getBlock();
				boolean shouldSpread = false;
				for(BlockFace face : nearbyFaces) {
					Block relative = bseBlock.getRelative(face);
					Material relativeType = relative.getType();
					if(relativeType == Material.CARPET || relativeType == Material.WALL_SIGN || relativeType == Material.SIGN_POST)
						relative.setType(Material.FIRE);
					else if(relativeType.isSolid()) shouldSpread = true;
				}
				if(!shouldSpread) bse.setCancelled(true);
				break;
			case "BlockFadeEvent":
				BlockFadeEvent bfe = (BlockFadeEvent)event;
				Block bfeBlock = bfe.getNewState().getBlock();
				boolean shouldFade = true;
				for(BlockFace face : nearbyFaces) {
					if(bfeBlock.getRelative(face).getType().isSolid()) {
						shouldFade = false;
						break;
					}
				}
				if(!shouldFade) bfe.setCancelled(true);
				break;
			case "BlockCanBuildEvent":
				BlockCanBuildEvent bcbe = (BlockCanBuildEvent)event;
				Material type = bcbe.getBlock().getType();
				if(type == Material.CARPET || type == Material.WALL_SIGN || type == Material.SIGN_POST || type == Material.FLOWER_POT ||
						type == Material.FLOWER_POT_ITEM) bcbe.setBuildable(true);
				break;
			case "HangingBreakEvent":
			case "BlockBurnEvent":
			case "EntityRegainHealthEvent":
				((Cancellable)event).setCancelled(true);
				break;
			default:
				break;
		}
	}
	
}
