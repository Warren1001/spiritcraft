package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.kabutils.spigot.event.GlobalListener;
import com.kabryxis.spiritcraft.game.ParticleTask;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.event.PlayerChangedDimEvent;
import com.kabryxis.spiritcraft.game.a.objective.Objective;
import com.kabryxis.spiritcraft.game.a.objective.ObjectiveTrigger;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.particle.ParticleEffect;

import java.util.HashMap;
import java.util.Map;

public class GameListener implements GlobalListener {
	
	private final Map<Entity, Long> lastPortalTimestamp = new HashMap<>();
	
	private final Game game;
	
	public GameListener(Game game) {
		this.game = game;
	}
	
	@Override
	public void onEvent(Event event) {
		switch(event.getEventName()) {
			case "EntityDamageByEntityEvent":
				EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)event;
				SpiritPlayer defender = game.getPlayerManager().getPlayer((Player)edbee.getEntity());
				SpiritPlayer attacker = null;
				Entity damager = edbee.getDamager();
				if(damager instanceof Player) attacker = game.getPlayerManager().getPlayer((Player)edbee.getDamager());
				else if(damager instanceof Projectile) attacker = game.getPlayerManager().getPlayer((Player)((Projectile)damager).getShooter());
				else break;
				if(defender.getPlayerType() != PlayerType.GHOST) break;
				Location loc = defender.getPlayer().getLocation().add(0, 0.75, 0);
				ParticleEffect.BLOCK_DUST.sendData(defender.getPlayer().getWorld().getPlayers(), loc.getX(), loc.getY(), loc.getZ(), 0.2, 0.66, 0.2, 0, 50, new ItemStack(Material.REDSTONE_BLOCK));
				defender.getPlayer().getWorld().playSound(loc, Sound.ZOMBIE_HURT, 1F, 0.1F);
				break;
			case "EntityDamageEvent":
				EntityDamageEvent ede = (EntityDamageEvent)event;
				if(ede.getCause() == EntityDamageEvent.DamageCause.FALL && game.getPlayerManager().getPlayer((Player)ede.getEntity()).getPlayerType() == PlayerType.GHOST) ede.setCancelled(true);
				break;
			case "EntityPortalEnterEvent":
				EntityPortalEnterEvent epee = (EntityPortalEnterEvent)event;
				Entity epeeEntity = epee.getEntity();
				long curr = System.currentTimeMillis();
				if(curr - lastPortalTimestamp.getOrDefault(epeeEntity, 0L) > 1000) {
					lastPortalTimestamp.put(epeeEntity, curr);
					//game.getCurrentArenaData().teleportToOtherDim(epeeEntity);
				}
				break;
			case "PlayerInteractEvent":
				PlayerInteractEvent pie = (PlayerInteractEvent)event;
				Action action = pie.getAction();
				if(action != Action.PHYSICAL) {
					SpiritPlayer player = game.getPlayerManager().getPlayer(pie.getPlayer());
					AbilityTrigger trigger = new AbilityTrigger();
					trigger.triggerer = player;
					trigger.type = action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR ? TriggerType.LEFT_CLICK : TriggerType.RIGHT_CLICK;
					trigger.hand = pie.getItem();
					trigger.block = pie.getClickedBlock();
					game.getAbilityManager().handle(player, trigger);
					if(trigger.cancel) pie.setCancelled(true);
				}
				if(pie.getAction() == Action.RIGHT_CLICK_BLOCK) {
					SpiritPlayer player = game.getPlayerManager().getPlayer(pie.getPlayer());
					Objective objective = game.getCurrentArenaData().getObjective(pie.getClickedBlock());
					if(objective != null) {
						objective.trigger(player, ObjectiveTrigger.RIGHT_CLICK);
						pie.setCancelled(true);
						return;
					}
				}
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
			case "EntityRegainHealthEvent":
				((Cancellable)event).setCancelled(true);
				break;
			default:
				break;
		}
	}
	
}
