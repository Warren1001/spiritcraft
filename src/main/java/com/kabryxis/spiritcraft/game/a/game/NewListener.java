package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.kabutils.spigot.event.GlobalListener;
import com.kabryxis.spiritcraft.Spiritcraft;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class NewListener implements GlobalListener {
	
	private final Spiritcraft plugin;
	private final Game game;
	
	public NewListener(Spiritcraft plugin) {
		this.plugin = plugin;
		this.game = plugin.getGame();
	}
	
	@Override
	public void onEvent(Event event) {
		switch(event.getEventName()) {
			/*case "PlayerInteractEvent":
				PlayerInteractEvent pie = (PlayerInteractEvent)event;
				if(pie.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Block block = pie.getClickedBlock();
					Location loc = block.getLocation();
					Location center = pie.getPlayer().getLocation();
					Material type = block.getType();
					byte data = block.getData();
					block.setType(Material.AIR);
					FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(loc, type, data);
					ArmorStand armorStand = block.getWorld().spawn(loc, ArmorStand.class);
					armorStand.setGravity(false);
					armorStand.setVisible(false);
					armorStand.setPassenger(fallingBlock);
					double motX = center.getX() - loc.getX();
					double motY = center.getY() - loc.getY();
					double motZ = center.getZ() - loc.getZ();
					double smallest = Double.min(motX, Double.min(motY, motZ));
					motX /= smallest;
					motY /= smallest;
					motZ /= smallest;
					motX /= 95.5093480923740239487239479238492374982543234233;
					motY /= 95.5093480923740239487239479238492374982543234233;
					motZ /= 95.5093480923740239487239479238492374982543234233;
					double movX = motX, movY = motY, movZ = motZ;
					BukkitThreads.syncTimer(new BukkitRunnable() {
						
						private int ticks = 0;
						
						@Override
						public void run() {
							((CraftArmorStand)armorStand).getHandle().move(movX, movY, movZ);
							//EntityFallingBlock entityFallingBlock = ((CraftFallingSand)fallingBlock).getHandle();
							//entityFallingBlock.setPositionRotation(entityFallingBlock.locX, entityFallingBlock.locY, entityFallingBlock.locZ, entityFallingBlock.yaw + motYaw, entityFallingBlock.pitch + motPitch);
							ticks++;
							if(ticks == 120) {
								fallingBlock.eject();
								armorStand.eject();
								armorStand.remove();
								fallingBlock.remove();
								cancel();
							}
						}
						
					}, 0L, 1L);
					pie.setCancelled(true);
				}
				break;*/
			case "PlayerJoinEvent":
				PlayerJoinEvent pje = (PlayerJoinEvent)event;
				Player pjeBukkitPlayer = pje.getPlayer();
				game.getPlayerManager().getPlayer(pjeBukkitPlayer).updatePlayer(pjeBukkitPlayer);
				break;
			case "PlayerSpawnLocationEvent":
				PlayerSpawnLocationEvent psle = (PlayerSpawnLocationEvent)event;
				psle.setSpawnLocation(plugin.getGame().getSpawn());
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
				if(edbbe.getCause() == EntityDamageEvent.DamageCause.VOID) edbbeEntity.teleport(plugin.getGame().getSpawn());
				edbbe.setCancelled(true);
				break;
			case "EntitySpawnEvent":
				/*EntitySpawnEvent ese = (EntitySpawnEvent)event;
				Entity eseEntity = ese.getEntity();
				if(!(eseEntity instanceof Player)) ese.setCancelled(true);
				break;*/
			case "EntityDamageEvent":
			case "EntityDamageByEntityEvent":
			case "WeatherChangeEvent":
			case "FoodLevelChangeEvent":
			case "PlayerDropItemEvent":
			case "PlayerPickupItemEvent":
			case "BlockBreakEvent":
			case "BlockPlaceEvent":
				((Cancellable)event).setCancelled(true);
				break;
			default:
				//System.out.println(event.getEventName());
				break;
		}
		if(plugin.getGame().isInProgress()) plugin.getGame().onEvent(event);
	}
	
}
