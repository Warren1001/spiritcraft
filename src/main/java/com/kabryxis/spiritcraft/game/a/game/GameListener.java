package com.kabryxis.spiritcraft.game.a.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import com.kabryxis.kabutils.data.Maps;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.kabutils.spigot.listener.GlobalListener;
import com.kabryxis.spiritcraft.game.ParticleTask;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownEntry;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownHandler;
import com.kabryxis.spiritcraft.game.a.event.PlayerChangedDimEvent;
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
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Openable;
import org.inventivetalent.particle.ParticleEffect;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GameListener implements GlobalListener {
	
	private final Set<Entity> lastPortalTimestamp = Sets.newSetFromMap(Maps.getFromCache(CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS)));
	private final Map<Player, Action> lastInteractTimestamp = Maps.getFromCache(CacheBuilder.newBuilder().expireAfterWrite(50, TimeUnit.MILLISECONDS));
	
	private final SpiritGame game;
	
	public GameListener(SpiritGame game) {
		this.game = game;
	}
	
	@Override
	public void onEvent(Event event) {
		switch(event.getEventName()) {
			case "EntityDamageByEntityEvent":
				EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)event;
				if(!(edbee.getEntity() instanceof Player)) break;
				SpiritPlayer defender = game.getPlayerManager().getPlayer((Player)edbee.getEntity());
				SpiritPlayer attacker = null;
				Entity damager = edbee.getDamager();
				if(damager instanceof Player) attacker = game.getPlayerManager().getPlayer((Player)edbee.getDamager());
				else if(damager instanceof Projectile) attacker = game.getPlayerManager().getPlayer((Player)((Projectile)damager).getShooter());
				else break;
				if(defender.getPlayerType() != PlayerType.GHOST) break;
				Location loc = defender.getLocation().add(0, 0.75, 0);
				ParticleEffect.BLOCK_DUST.sendData(defender.getPlayer().getWorld().getPlayers(), loc.getX(), loc.getY(), loc.getZ(), 0.2, 0.66, 0.2, 0, 50, new ItemStack(Material.REDSTONE_BLOCK));
				defender.getPlayer().getWorld().playSound(loc, Sound.ZOMBIE_HURT, 1F, 0.1F); // TODO call by soundmanager
				break;
			case "EntityDamageEvent":
				EntityDamageEvent ede = (EntityDamageEvent)event;
				Entity edeEntity = ede.getEntity();
				if(ede.getCause() == EntityDamageEvent.DamageCause.FALL && edeEntity instanceof Player &&
						game.getPlayerManager().getPlayer((Player)edeEntity).getPlayerType() == PlayerType.GHOST) ede.setCancelled(true);
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
				Player bukkitPlayer = pie.getPlayer();
				Action action = pie.getAction();
				if(lastInteractTimestamp.get(bukkitPlayer) == action) break;
				if(action != Action.PHYSICAL) {
					Block block = pie.getClickedBlock();
					if(action == Action.RIGHT_CLICK_BLOCK && (block.getState() instanceof InventoryHolder || block.getState().getData() instanceof Openable)) break; // TODO
					SpiritPlayer player = game.getPlayerManager().getPlayer(bukkitPlayer);
					ConfigSection triggerData = new ConfigSection();
					CooldownHandler cooldownHandler = new CooldownHandler(player.getCooldownManager());
					triggerData.put("cooldownHandler", cooldownHandler);
					triggerData.put("triggerer", player);
					triggerData.put("target", player);
					triggerData.put("type", action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR ? TriggerType.LEFT_CLICK : TriggerType.RIGHT_CLICK);
					triggerData.put("hand", pie.getItem());
					triggerData.put("block", block);
					triggerData.put("abilityId", Items.getTagData(pie.getItem(), "AbiId", Integer.class, 0));
					long cooldown = Items.getTagData(pie.getItem(), "cd", Long.class, 0L);
					if(cooldown > 0L) cooldownHandler.setCooldown(new CooldownEntry(cooldown, triggerData));
					game.getAbilityManager().perform(triggerData);
					game.getObjectiveManager().perform(triggerData);
					if(triggerData.getBoolean("cancel", false)) pie.setCancelled(true);
				}
				lastInteractTimestamp.put(bukkitPlayer, action);
				break;
			case "PlayerChangedDimEvent":
				PlayerChangedDimEvent pcde = (PlayerChangedDimEvent)event;
				SpiritPlayer pcdePlayer = pcde.getPlayer();
				if(pcdePlayer.getPlayerType() == PlayerType.GHOST) {
					ParticleTask particleTask = pcdePlayer.getParticleTask();
					if(pcde.getNewDim() == PlayerChangedDimEvent.DimType.NORMAL) particleTask.setDefaultDelay();
					else particleTask.setDelay(particleTask.getDelay() / 8);
				}
				break;
			case "BlockSpreadEvent":
				BlockSpreadEvent bse = (BlockSpreadEvent)event;
				Block bseBlock = bse.getBlock();
				boolean shouldSpread = false;
				for(BlockFace face : Arrays.asList(BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH)) { // TODO
					Block relative = bseBlock.getRelative(face);
					Material relativeType = relative.getType();
					if(relativeType == Material.CARPET || relativeType == Material.WALL_SIGN) {
						relative.setType(Material.FIRE);
						break;
					}
					if(relativeType.isSolid()) {
						shouldSpread = true;
						break;
					}
				}
				if(!shouldSpread) bse.setCancelled(true);
				break;
			case "BlockFadeEvent":
				BlockFadeEvent bfe = (BlockFadeEvent)event;
				Block bfeBlock = bfe.getNewState().getBlock();
				boolean shouldFade = true;
				for(BlockFace face : Arrays.asList(BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH)) { // TODO
					Block relative = bfeBlock.getRelative(face);
					Material relativeType = relative.getType();
					if(relativeType.isSolid()) {
						shouldFade = false;
						break;
					}
				}
				if(!shouldFade) bfe.setCancelled(true);
				break;
			case "BlockBurnEvent":
			case "EntityRegainHealthEvent":
				((Cancellable)event).setCancelled(true);
				break;
			default:
				break;
		}
	}
	
}
