package com.kabryxis.spiritcraft.game.object.type.objective.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.sk89q.worldedit.Vector;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.Random;

public class ExplodeAction extends SpiritGameObjectAction {
	
	private final Block objectiveBlock;
	
	private Vector min, max, center;
	private int protectX, protectZ;
	
	public ExplodeAction(ConfigSection creatorData) {
		super(creatorData, "explode");
		this.objectiveBlock = creatorData.get("objectiveBlock");
		handleSubCommand("min", true, Vector.class, data -> min = data);
		handleSubCommand("max", true, Vector.class, data -> max = data);
		handleSubCommand("center", true, Vector.class, data -> center = data);
		handleSubCommand("protect", true, true, data -> {
			String[] dataArgs = data.split(",");
			protectX = Integer.parseInt(dataArgs[0]);
			protectZ = Integer.parseInt(dataArgs[1]);
		});
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		Random rand = new Random();
		Block block = objectiveBlock;
		World world = block.getWorld();
		world.playSound(block.getLocation(), Sound.FIZZ, 5F, 0.5F);
		Location center = game.getCurrentArenaData().toLocation(this.center);
		game.getTaskManager().start(() -> {
			world.playSound(center, Sound.EXPLODE, 5F, 0.5F);
			world.playEffect(center, Effect.EXPLOSION_HUGE, 10);
			for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
				for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
					for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
						Block b = block.getRelative(x, y, z);
						if((x != protectX && z != protectZ) && (y == min.getBlockY() || y == max.getBlockY()) && rand.nextInt(4) == 0) continue;
						Material type = block.getType();
						byte data = block.getData();
						block.setType(Material.AIR);
						if(x == protectX && z == protectZ) continue;
						Location loc = block.getLocation().add(0.5, 0.5, 0.5);
						FallingBlock fallingBlock = world.spawnFallingBlock(loc, type, data);
						org.bukkit.util.Vector velocity = loc.subtract(center).toVector().normalize();
						if(rand.nextBoolean()) velocity.setX((velocity.getX() * 1.5) + ((rand.nextInt(5) - 2) / 7.5));
						else velocity.setZ((velocity.getZ() * 1.5) + ((rand.nextInt(5) - 2) / 7.5));
						velocity.setY(velocity.getY() / 1.5);
						fallingBlock.setVelocity(velocity);
					}
				}
			}
		}, 40L);
	}
	
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		Location loc = event.getBlock().getLocation().subtract(objectiveBlock.getLocation());
		if(loc.getBlockX() == protectX && loc.getBlockZ() == protectZ) event.setCancelled(true);
	}
	
}
