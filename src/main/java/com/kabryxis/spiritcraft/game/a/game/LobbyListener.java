package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.kabutils.spigot.event.GlobalListener;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class LobbyListener implements GlobalListener {
	
	private final Game game;
	
	private int allowNextItemsSpawn = 0;
	
	public LobbyListener(Game game) {
		this.game = game;
	}
	
	public void allowNextItemSpawn() {
		allowNextItemsSpawn++;
	}
	
	@Override
	public void onEvent(Event event) {
		switch(event.getEventName()) {
			case "PlayerJoinEvent":
				PlayerJoinEvent pje = (PlayerJoinEvent)event;
				Player pjeBukkitPlayer = pje.getPlayer();
				game.getPlayerManager().getPlayer(pjeBukkitPlayer).updatePlayer(pjeBukkitPlayer);
				break;
			case "PlayerSpawnLocationEvent":
				PlayerSpawnLocationEvent psle = (PlayerSpawnLocationEvent)event;
				psle.setSpawnLocation(game.getSpawn());
				break;
			case "PlayerQuitEvent":
				PlayerQuitEvent pqe = (PlayerQuitEvent)event;
				Player pqeBukkitPlayer = pqe.getPlayer();
				SpiritPlayer pqePlayer = game.getPlayerManager().getPlayer(pqeBukkitPlayer);
				if(pqePlayer.getPlayerType() == PlayerType.WAITING) {
					// TODO handle all the other playerTypes too
				}
				break;
			case "CreatureSpawnEvent":
				CreatureSpawnEvent cse = (CreatureSpawnEvent)event;
				if(cse.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) cse.setCancelled(true);
				break;
			case "EntityDamageByBlockEvent":
				EntityDamageByBlockEvent edbbe = (EntityDamageByBlockEvent)event;
				Entity edbbeEntity = edbbe.getEntity();
				if(edbbe.getCause() == EntityDamageEvent.DamageCause.VOID) {
					if(edbbeEntity instanceof Player && game.getPlayerManager().getPlayer((Player)edbbeEntity).isInGame()) break;
					edbbeEntity.teleport(game.getSpawn());
				}
				edbbe.setCancelled(true);
				break;
			case "ItemSpawnEvent": // TODO figure out how to get rid of item drops from blocks
			case "EntitySpawnEvent":
				EntitySpawnEvent ese = (EntitySpawnEvent)event;
				Entity entity = ese.getEntity();
				if(entity instanceof Item && allowNextItemsSpawn > 0) {
					allowNextItemsSpawn--;
					break;
				}
				if(!(entity instanceof Player) && !(entity instanceof Projectile)) ese.setCancelled(true);
				break;
			case "PlayerDeathEvent":
				PlayerDeathEvent pde = (PlayerDeathEvent)event;
				pde.setDeathMessage("");
				pde.getDrops().clear();
				break;
			case "WeatherChangeEvent":
				WeatherChangeEvent wce = (WeatherChangeEvent)event;
				wce.getWorld().setWeatherDuration(0);
				if(wce.toWeatherState()) wce.setCancelled(true);
				break;
			case "PlayerToggleSneakEvent":
				PlayerToggleSneakEvent ptse = (PlayerToggleSneakEvent)event;
				if(ptse.getPlayer().getGameMode() == GameMode.SPECTATOR) ptse.setCancelled(true);
				break;
			case "PlayerGameModeChangeEvent":
				PlayerGameModeChangeEvent pgmce = (PlayerGameModeChangeEvent)event;
				if(pgmce.getNewGameMode() == GameMode.SPECTATOR) {
					BukkitThreads.sync(() -> {
						pgmce.getPlayer().setSpectatorTarget(Bukkit.getPlayer("SecondWind"));
					});
				}
				break;
			case "EntityDamageEvent":
			case "EntityDamageByEntityEvent":
			case "FoodLevelChangeEvent":
			case "ItemMergeEvent":
			case "ThunderChangeEvent":
				//case "PlayerDropItemEvent":
				//case "PlayerPickupItemEvent":
				//case "BlockBreakEvent":
				//case "BlockPlaceEvent":
				((Cancellable)event).setCancelled(true);
				break;
			case "ChunkUnloadEvent":
				//case "PlayerMoveEvent":
			case "BlockPhysicsEvent":
				//case "PlayerAnimationEvent":
			case "ChunkLoadEvent":
				//case "PlayerStatisticIncrementEvent":
				//case "PlayerToggleSprintEvent":
			case "WorldSaveEvent":
				//case "PlayerItemHeldEvent":
			case "EntityPortalEnterEvent":
			case "PlayerChangedDimEvent":
			case "PlayerChangedWorldEvent":
			case "PlayerVelocityEvent":
				//case "PlayerInteractEvent":
				//case "BlockDamageEvent":
				break;
			default:
				//System.out.println(event.getEventName());
				break;
		}
		if(game.isInProgress()) game.onEvent(event);
	}
	
}
