package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.kabutils.spigot.listener.GlobalListener;
import com.kabryxis.spiritcraft.game.a.world.schematic.SchematicDataCreator;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.sk89q.worldedit.Vector;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class LobbyListener implements GlobalListener {
	
	private final SpiritGame game;
	
	private int allowNextItemsSpawn = 0;
	
	public LobbyListener(SpiritGame game) {
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
				pje.setJoinMessage(null);
				game.getPlayerManager().getPlayer(pje.getPlayer()).updatePlayer(pje.getPlayer());
				break;
			case "PlayerSpawnLocationEvent":
				PlayerSpawnLocationEvent psle = (PlayerSpawnLocationEvent)event;
				psle.setSpawnLocation(game.getSpawn());
				break;
			case "PlayerQuitEvent":
				PlayerQuitEvent pqe = (PlayerQuitEvent)event;
				pqe.setQuitMessage(null);
				SpiritPlayer pqePlayer = game.getPlayerManager().getPlayer(pqe.getPlayer());
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
			case "ItemSpawnEvent":
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
				pde.setDeathMessage(null);
				pde.getDrops().clear();
				break;
			case "WeatherChangeEvent":
				WeatherChangeEvent wce = (WeatherChangeEvent)event;
				if(wce.toWeatherState()) wce.setCancelled(true);
				break;
			case "PlayerToggleSneakEvent":
				PlayerToggleSneakEvent ptse = (PlayerToggleSneakEvent)event;
				if(ptse.getPlayer().getGameMode() == GameMode.SPECTATOR) ptse.setCancelled(true);
				break;
			case "PlayerPickupItemEvent":
				PlayerPickupItemEvent ppie = (PlayerPickupItemEvent)event;
				if(ppie.getItem().hasMetadata("nopickup")) ppie.setCancelled(true);
				break;
			case "PlayerInteractEvent":
				PlayerInteractEvent pie = (PlayerInteractEvent)event;
				if(pie.getAction() == Action.LEFT_CLICK_BLOCK || pie.getAction() == Action.RIGHT_CLICK_BLOCK) {
					SpiritPlayer player = game.getPlayerManager().getPlayer(pie.getPlayer());
					SchematicDataCreator dataCreator = player.getDataCreator();
					if(dataCreator.printOffsetLocation()) {
						Vector offset = dataCreator.getOffsetLocation(pie.getClickedBlock().getLocation());
						player.sendMessage(offset.getX() + "," + offset.getY() + "," + offset.getZ());
					}
				}
				break;
			case "FoodLevelChangeEvent":
				FoodLevelChangeEvent flce = (FoodLevelChangeEvent)event;
				flce.setFoodLevel(20);
				((Player)flce.getEntity()).setSaturation(Float.MAX_VALUE);
				break;
			case "PlayerDropItemEvent":
				game.getPlugin().allowNextItemSpawn();
				break;
			case "EntityDamageByEntityEvent":
			case "EntityDamageEvent":
				EntityDamageEvent ede = (EntityDamageEvent)event;
				Entity edeEntity = ede.getEntity();
				if(!(edeEntity instanceof Player) || game.getPlayerManager().getPlayer((Player)edeEntity).getPlayerType() == PlayerType.WAITING) ede.setCancelled(true);
				break;
			case "BlockPhysicsEvent":
				BlockPhysicsEvent bpe = (BlockPhysicsEvent)event;
				if(bpe.getBlock().getType() == Material.REDSTONE_LAMP_ON && bpe.getChangedType() == Material.REDSTONE_LAMP_OFF) {
					System.out.println("found and cancelled a blockphysicsevent for redstone lamp");
					bpe.setCancelled(true);
				}
				break;
			case "ItemMergeEvent":
			case "ThunderChangeEvent":
				((Cancellable)event).setCancelled(true);
				break;
			case "ChunkUnloadEvent":
			case "PlayerMoveEvent":
			//case "BlockPhysicsEvent":
			case "PlayerAnimationEvent":
			case "ChunkLoadEvent":
			case "PlayerStatisticIncrementEvent":
			case "PlayerToggleSprintEvent":
			case "WorldSaveEvent":
			case "PlayerItemHeldEvent":
			case "EntityPortalEnterEvent":
			case "PlayerChangedDimEvent":
			case "PlayerChangedWorldEvent":
			case "PlayerVelocityEvent":
			case "BlockDamageEvent":
				break;
			default:
				break;
		}
		if(game.isInProgress()) game.onEvent(event);
	}
	
}
