package com.kabryxis.spiritcraft.game;

import com.kabryxis.kabutils.spigot.listener.Listeners;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashMap;
import java.util.Map;

public class DeadBodyManager implements Listener {
	
	private final Map<SpiritPlayer, DeadBody> deadBodies = new HashMap<>();
	
	private final SpiritGame game;
	
	public DeadBodyManager(SpiritGame game) {
		this.game = game;
		Listeners.registerListener(this, game.getPlugin());
	}
	
	public DeadBody getDeadBody(SpiritPlayer player) {
		return deadBodies.computeIfAbsent(player, DeadBody::new);
	}
	
	public void died(SpiritPlayer player) {
		deadBodies.get(player).died();
	}
	
	public void revive(SpiritPlayer player) {
		deadBodies.get(player).revive();
	}
	
	public void show(SpiritPlayer player) {
		deadBodies.values().stream().filter(deadBody -> deadBody.isSpawned() && player.getWorld().equals(deadBody.getDeathWorld())).forEach(deadBody -> deadBody.show(player));
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		String customName = entity.getCustomName();
		if(entity.getType() == EntityType.SLIME && customName != null) {
			Entity damager = event.getDamager();
			if(damager instanceof Player) {
				Player attacker = (Player)damager;
				if(attacker.getName().equals(customName)) revive(game.getPlayerManager().getPlayer(attacker));
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		String customName = entity.getCustomName();
		if(entity.getType() == EntityType.SLIME && customName != null) {
			Player player = event.getPlayer();
			if(player.getName().equals(customName)) revive(game.getPlayerManager().getPlayer(player));
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		show(game.getPlayerManager().getPlayer(event.getPlayer()));
	}
	
}
