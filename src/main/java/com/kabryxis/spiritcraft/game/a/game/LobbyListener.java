package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.spiritcraft.Spiritcraft;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.spigot.event.GlobalListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LobbyListener implements GlobalListener {
	
	private final Spiritcraft plugin;
	
	public LobbyListener(Spiritcraft plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onEvent(Event event) {
		switch(event.getEventName()) {
			case "PlayerJoinEvent":
				PlayerJoinEvent pje = (PlayerJoinEvent)event;
				// TODO joining here has no effect because they weren't already in a game, send them to lobby
				// TODO PlayerSendLobbyEvent from a global PJE
				break;
			case "PlayerQuitEvent":
				PlayerQuitEvent pqe = (PlayerQuitEvent)event;
				// TODO quitting from here has no relation to it being intentional, player has no data so reset what already may exist and get rid of
				break;
			case "PlayerChangedWorldEvent":
				PlayerChangedWorldEvent pcwe = (PlayerChangedWorldEvent)event;
				Player pcwePlayer = pcwe.getPlayer();
				if(pcwe.getFrom().getName().equals("spirit_lobby")) {
					if(!pcwePlayer.getWorld().getName().startsWith("spirit_")) { // intentional leave TODO better detect worlds
						plugin.getGame().getPlayerManager().getPlayer(pcwePlayer).getData().save();
					}
				}
				else if(pcwePlayer.getWorld().getName().equals("spirit_lobby")) {
					if(!pcwe.getFrom().getName().startsWith("spirit_")) { // intentional join
						plugin.getGame().getPlayerManager().getPlayer(pcwePlayer).updatePlayer(pcwePlayer); // preloads player object
						// TODO
					}
				}
				break;
			case "PlayerInteractEvent": // TODO
				PlayerInteractEvent pie = (PlayerInteractEvent)event;
				SpiritPlayer player = plugin.getGame().getPlayerManager().getPlayer(pie.getPlayer());
				Action action = pie.getAction();
				break;
			case "CreatureSpawnEvent":
				CreatureSpawnEvent cse = (CreatureSpawnEvent)event;
				if(cse.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) cse.setCancelled(true);
				break;
			case "EntityDamageEvent":
				EntityDamageEvent ede = (EntityDamageEvent)event;
				Entity edeEntity = ede.getEntity();
				if(edeEntity instanceof Player && ede.getCause() == EntityDamageEvent.DamageCause.VOID) {
					ede.setCancelled(true);
					edeEntity.teleport(plugin.getGame().getSpawn());
				}
				break;
			case "EntitySpawnEvent":
				EntitySpawnEvent ese = (EntitySpawnEvent)event;
				Entity eseEntity = ese.getEntity();
				if(!(eseEntity instanceof Player)) ese.setCancelled(true);
				break;
			case "WeatherChangeEvent":
			case "FoodLevelChangeEvent":
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
	
}
