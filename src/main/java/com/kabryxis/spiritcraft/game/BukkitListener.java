package com.kabryxis.spiritcraft.game;

import com.kabryxis.kabutils.spigot.event.GlobalListener;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.spiritcraft.Spiritcraft;
import com.kabryxis.spiritcraft.game.ability.CloudTask;
import com.kabryxis.spiritcraft.game.ability.FireBreathTask;
import com.kabryxis.spiritcraft.game.ability.ThrowItemDelayedRunnable;
import com.kabryxis.spiritcraft.game.ability.ThrowItemTimerRunnable;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.inventivetalent.particle.ParticleEffect;

public class BukkitListener implements GlobalListener {
	
	private final Spiritcraft plugin;
	private final String worldName;
	
	public BukkitListener(Spiritcraft plugin, String worldName) {
		this.plugin = plugin;
		this.worldName = worldName;
	}
	
	@Override
	public void onEvent(Event event) {
		switch(event.getEventName()) {
			case "PlayerJoinEvent":
				PlayerJoinEvent pje = (PlayerJoinEvent)event;
				plugin.getGame().getPlayerManager().getPlayer(pje.getPlayer()); // preloads player object
				break;
			case "PlayerQuitEvent":
				PlayerQuitEvent pqe = (PlayerQuitEvent)event;
				plugin.getGame().getPlayerManager().getPlayer(pqe.getPlayer()).getData().save();
				break;
			case "PlayerInteractEvent":
				PlayerInteractEvent pie = (PlayerInteractEvent)event;
				SpiritPlayer player = plugin.getGame().getPlayerManager().getPlayer(pie.getPlayer());
				Action action = pie.getAction();
				if(Items.isType(pie.getItem(), Material.STONE_AXE)) {
					if(action == Action.LEFT_CLICK_BLOCK) {
						player.getSelection().setLeft(pie.getClickedBlock().getLocation());
						pie.setCancelled(true);
					}
					else if(action == Action.RIGHT_CLICK_BLOCK) {
						player.getSelection().setRight(pie.getClickedBlock().getLocation());
						pie.setCancelled(true);
					}
				}
				else if(Items.isType(pie.getItem(), Material.STICK)) {
					if(action == Action.LEFT_CLICK_BLOCK) {
						player.getSelection().addBlock(pie.getClickedBlock());
						pie.setCancelled(true);
					}
					else if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
						if(player.getPlayer().isSneaking()) player.getSelection().addSelection(false);
						else player.getSelection().addSelection(true);
						pie.setCancelled(true);
					}
				}
				else if(Items.isType(pie.getItem(), Material.STRING)) {
					if(action == Action.LEFT_CLICK_BLOCK) {
						player.getSelection().removeBlock(pie.getClickedBlock());
						pie.setCancelled(true);
					}
					else if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
						player.getSelection().removeSelection();
						pie.setCancelled(true);
					}
				}
				else if(Items.isType(pie.getItem(), Material.SHEARS)) {
					if(action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) player.startCharge(pie.getItem());
				}
				else if(Items.isType(pie.getItem(), Material.NETHER_STAR)) {
					if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
						new ThrowItemDelayedRunnable(player, 3 * 20L) {
							
							@Override
							public void onThrow() {}
							
							@Override
							public void onFinish() {
								Location loc = item.getLocation();
								loc.getWorld().playSound(loc, Sound.GLASS, 1F, 0.01F);
								loc.getWorld().getNearbyEntities(loc, 3.5, 3.5, 3.5).forEach(entity -> {
									if(entity instanceof Player) {
										SpiritPlayer player = plugin.getGame().getPlayerManager().getPlayer((Player)entity);
										if(player.getPlayerType() == PlayerType.HUNTER) player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 0, 3 * 20), true);
									}
								});
							}
							
						};
					}
				}
				else if(Items.isType(pie.getItem(), Material.BLAZE_POWDER)) {
					if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
						player.getInventory().setItemInHand(new ItemStack(Material.AIR));
						player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 0.01F);
						new FireBreathTask(player);
					}
				}
				else if(Items.isType(pie.getItem(), Material.WOOL)) {
					if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
						new ThrowItemDelayedRunnable(player, 2 * 20L) {
							
							@Override
							public void onThrow() {}
							
							@Override
							public void onFinish() {
								Location loc = item.getLocation();
								loc.getWorld().playSound(loc, Sound.DIG_SNOW, 1F, 0.5F);
								new CloudTask(loc);
							}
							
						};
					}
					else if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
						Location loc = player.getLocation();
						loc.getWorld().playSound(loc, Sound.DIG_SNOW, 1F, 0.5F);
						new CloudTask(loc);
						player.getInventory().setItemInHand(new ItemStack(Material.AIR));
					}
				}
				else if(Items.isType(pie.getItem(), Material.MAGMA_CREAM)) {
					if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
						new ThrowItemTimerRunnable(player, 10L, 3000) {
							
							@Override
							public void onThrow() {}
							
							@Override
							public void onTick() {
								item.getWorld().playSound(item.getLocation(), Sound.NOTE_PIANO, 0.5F, 1F);
							}
							
							@Override
							public void onFinish() {
								Location loc = item.getLocation();
								loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 4F, false, false); // TODO custom explosion damage handling to get who caused the dmg
							}
							
						};
					}
				}
				break;
			case "EntityDamageByEntityEvent":
				EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)event;
				SpiritPlayer defender = plugin.getGame().getPlayerManager().getPlayer((Player)edbee.getEntity());
				SpiritPlayer attacker = null;
				Entity damager = edbee.getDamager();
				if(damager instanceof Player) attacker = plugin.getGame().getPlayerManager().getPlayer((Player)edbee.getDamager());
				else if(damager instanceof Projectile) attacker = plugin.getGame().getPlayerManager().getPlayer((Player)((Projectile)damager).getShooter());
				else break;
				if(defender.getPlayerType() != PlayerType.GHOST) break;
				Location loc = defender.getPlayer().getLocation().add(0, 0.75, 0);
				ParticleEffect.BLOCK_DUST.sendData(defender.getPlayer().getWorld().getPlayers(), loc.getX(), loc.getY(), loc.getZ(), 0.2, 0.66, 0.2, 0, 50, new ItemStack(Material.REDSTONE_BLOCK));
				defender.getPlayer().getWorld().playSound(loc, Sound.ZOMBIE_HURT, 1F, 0.1F);
				break;
			case "EntityDamageEvent":
				EntityDamageEvent ede = (EntityDamageEvent)event;
				if(ede.getCause() == EntityDamageEvent.DamageCause.FALL && plugin.getGame().getPlayerManager().getPlayer((Player)ede.getEntity()).getPlayerType() == PlayerType.GHOST) ede.setCancelled(true);
				break;
			case "CreatureSpawnEvent":
				CreatureSpawnEvent cse = (CreatureSpawnEvent)event;
				if(cse.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) cse.setCancelled(true);
				break;
			case "ItemSpawnEvent":
			case "EntitySpawnEvent":
				EntitySpawnEvent ese = (EntitySpawnEvent)event;
				Entity entity = ese.getEntity();
				if(!(entity instanceof Player) && !(entity instanceof Item) && !(entity instanceof Projectile)) ese.setCancelled(true);
				break;
			case "PlayerDeathEvent":
				PlayerDeathEvent pde = (PlayerDeathEvent)event;
				pde.setDeathMessage("");
				pde.getDrops().clear();
				break;
			case "WeatherChangeEvent":
			case "FoodLevelChangeEvent":
			case "EntityRegainHealthEvent":
			case "PlayerDropItemEvent":
			case "PlayerPickupItemEvent":
			case "BlockBreakEvent":
			case "BlockPlaceEvent":
				((Cancellable)event).setCancelled(true);
				break;
			default:
				break;
		}
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if(event.getFrom().getName().equals(worldName)) {
			plugin.getGame().getPlayerManager().getPlayer(player).getData().save();
			// TODO this is intentionally quitting, do a full save / unload (the above PQE should keep stuff in memory incase of a sudden disconnect)
		}
		else if(event.getPlayer().getWorld().getName().equals(worldName)) {
			plugin.getGame().getPlayerManager().getPlayer(player).updatePlayer(player); // preloads player object
			// TODO if handled functionality determines player to always spawn in lobby world first, reset and reapply stuff to player if theyre rejoining game or make them spectator, ect
			// TODO if functionality isnt as above, do this in PJE above
		}
	}
	
}
