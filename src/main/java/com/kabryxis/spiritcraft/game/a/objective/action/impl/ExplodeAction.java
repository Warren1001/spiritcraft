package com.kabryxis.spiritcraft.game.a.objective.action.impl;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.a.objective.ObjectiveTrigger;
import com.kabryxis.spiritcraft.game.a.objective.action.ObjectiveAction;
import com.kabryxis.spiritcraft.game.a.objective.action.ObjectiveActionCreator;
import com.kabryxis.spiritcraft.game.a.world.DimData;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ExplodeAction implements ObjectiveActionCreator, ObjectiveAction, Listener {
	
	private final Map<Block, Data> dataMap = new HashMap<>();
	
	public ExplodeAction(Game game) {
		game.getPlugin().getServer().getPluginManager().registerEvents(this, game.getPlugin());
	}
	
	@Override
	public ExplodeAction create(DimData dimData, Block location, String data) {
		String[] infos = data.split(";");
		Location start = dimData.getDimInfo().getLocation();
		int minX = 0, minY = 0, minZ = 0;
		int maxX = 0, maxY = 0, maxZ = 0;
		Location center = null;
		int protectX = 0, protectZ = 0;
		for(String info : infos) {
			String[] infoArgs = info.split("~");
			String extraData = infoArgs[1];
			String[] dataArgs;
			switch(infoArgs[0]) {
				case "min":
					dataArgs = extraData.split(",");
					minX = start.getBlockX() + Integer.parseInt(dataArgs[0]);
					minY = start.getBlockY() + Integer.parseInt(dataArgs[1]);
					minZ = start.getBlockZ() + Integer.parseInt(dataArgs[2]);
					break;
				case "max":
					dataArgs = extraData.split(",");
					maxX = start.getBlockX() + Integer.parseInt(dataArgs[0]);
					maxY = start.getBlockY() + Integer.parseInt(dataArgs[1]);
					maxZ = start.getBlockZ() + Integer.parseInt(dataArgs[2]);
					break;
				case "center":
					dataArgs = extraData.split(",");
					center = start.clone().add(Double.parseDouble(dataArgs[0]), Double.parseDouble(dataArgs[1]), Double.parseDouble(dataArgs[2]));
					break;
				case "protect":
					dataArgs = extraData.split(",");
					protectX = Integer.parseInt(dataArgs[0]);
					protectZ = Integer.parseInt(dataArgs[1]);
					break;
				default:
					System.out.println(getClass().getSimpleName() + " does not know how to handle sub-action '" + infoArgs[0] + "', skipping.");
					break;
			}
		}
		dataMap.put(location, new Data(location, minX, minY, minZ, maxX, maxY, maxZ, center, protectX, protectZ));
		return this;
	}
	
	@Override
	public void perform(SpiritPlayer player, Block location, ObjectiveTrigger trigger) {
		dataMap.get(location).perform();
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		for(Data data : dataMap.values()) {
			if(data.isProtected(event.getBlock())) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	private class Data {
		
		private final Block block;
		private final int minX, minY, minZ;
		private final int maxX, maxY, maxZ;
		private final Location center;
		private final int protectX, protectZ;
		
		public Data(Block block, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Location center, int protectX, int protectZ) {
			this.block = block;
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
			this.center = center;
			this.protectX = protectX;
			this.protectZ = protectZ;
		}
		
		public void perform() {
			Random rand = new Random();
			World world = center.getWorld();
			block.getWorld().playSound(block.getLocation(), Sound.FIZZ, 5F, 0.5F);
			BukkitThreads.syncLater(() -> {
				world.playSound(center, Sound.EXPLODE, 5F, 0.5F);
				world.playEffect(center, Effect.EXPLOSION_HUGE, 10);
				for(int x = minX; x <= maxX; x++) {
					for(int z = minZ; z <= maxZ; z++) {
						for(int y = minY; y <= maxY; y++) {
							Block block = world.getBlockAt(x, y, z);
							if(!isProtected(block) && (y == minY || y == maxY) && rand.nextInt(4) == 0) continue;
							Material type = block.getType();
							byte data = block.getData();
							block.setType(Material.AIR);
							if(isProtected(block)) continue;
							Location loc = block.getLocation().add(0.5, 0.5, 0.5);
							FallingBlock fallingBlock = world.spawnFallingBlock(loc, type, data);
							Vector velocity = loc.subtract(center).toVector().normalize();
							if(rand.nextBoolean()) velocity.setX((velocity.getX() * 1.5) + ((rand.nextInt(5) - 2) / 7.5));
							else velocity.setZ((velocity.getZ() * 1.5) + ((rand.nextInt(5) - 2) / 7.5));
							velocity.setY(velocity.getY() / 1.5);
							fallingBlock.setVelocity(velocity);
						}
					}
				}
			}, 40L);
		}
		
		public boolean isProtected(Block block) {
			return block.getX() == protectX && block.getZ() == protectZ;
		}
		
	}
	
}
