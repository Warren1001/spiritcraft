package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.kabutils.spigot.listener.Listeners;
import com.kabryxis.spiritcraft.GridRenderer;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.map.MapView;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class LobbyListener implements Listener {
	
	private final SpiritGame game;
	
	public LobbyListener(SpiritGame game) {
		this.game = game;
		Listeners.cancelEvents(game.getPlugin(), ItemMergeEvent.class, ThunderChangeEvent.class);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		game.getPlayerManager().getPlayer(player).updatePlayer(player);
		// TODO load from combat logger
	}
	
	@EventHandler
	public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
		event.setSpawnLocation(game.getSpawn());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		SpiritPlayer pqePlayer = game.getPlayerManager().getPlayer(event.getPlayer());
		if(pqePlayer.getPlayerType() == PlayerType.WAITING) {
			// TODO handle all the other playerTypes too
		}
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Player && !game.isInProgress()) {
			event.setCancelled(true);
			if(event.getCause() == EntityDamageEvent.DamageCause.VOID) entity.teleport(game.getSpawn());
		}
	}
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		Entity entity = event.getEntity();
		if(!(entity instanceof Player) && !(entity instanceof Projectile)) event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
		event.getDrops().clear();
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		//if(event.toWeatherState()) event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) { // prevents players from freemoding in spectator mode
		if(event.getPlayer().getGameMode() == GameMode.SPECTATOR) event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if(event.getItem().hasMetadata("nopickup")) event.setCancelled(true);
	}
	
	/*@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if(action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK) {
			SpiritPlayer player = game.getPlayerManager().getPlayer(event.getPlayer());
			SchematicDataCreator dataCreator = player.getDataCreator();
			if(dataCreator.printOffsetLocation()) {
				Vector offset = dataCreator.getOffsetLocation(event.getClickedBlock().getLocation());
				player.sendMessage("%s,%s,%s", offset.getX(), offset.getY(), offset.getZ());
			}
		}
	}*/
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setFoodLevel(20);
		((Player)event.getEntity()).setSaturation(Float.MAX_VALUE);
	}
	
	@EventHandler
	public void onMapInitialize(MapInitializeEvent event) {
		MapView view = event.getMap();
		view.setScale(MapView.Scale.FARTHEST);
		view.getRenderers().forEach(view::removeRenderer);
		GridRenderer renderer = new GridRenderer(game.getPlugin());
		try {
			renderer.setGridImage(0, 0, ImageIO.read(new File(game.getPlugin().getDataFolder(), "images" + File.separator + "redstone_dust.png")));
		} catch(IOException e) {
			e.printStackTrace();
		}
		view.addRenderer(renderer);
	}
	
}
