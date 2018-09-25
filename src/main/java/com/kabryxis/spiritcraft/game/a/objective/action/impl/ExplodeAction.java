package com.kabryxis.spiritcraft.game.a.objective.action.impl;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.a.objective.Objective;
import com.kabryxis.spiritcraft.game.a.objective.ObjectiveTrigger;
import com.kabryxis.spiritcraft.game.a.objective.action.AbstractSpiritObjectiveAction;
import com.kabryxis.spiritcraft.game.a.objective.action.ObjectiveAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.sk89q.worldedit.Vector;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.Random;

public class ExplodeAction extends AbstractSpiritObjectiveAction {
	
	private Vector min, max, center;
	private int protectX, protectZ;
	
	public ExplodeAction(GameObjectManager<ObjectiveAction> objectManager) {
		super(objectManager, "explode");
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
	public void trigger(SpiritPlayer player, Block block, ObjectiveTrigger trigger) {
		Random rand = new Random();
		World world = block.getWorld();
		world.playSound(block.getLocation(), Sound.FIZZ, 5F, 0.5F);
		Location center = game.getCurrentArenaData().getArena().toLocation(this.center);
		BukkitThreads.syncLater(() -> {
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
	
	@Override
	public void event(Objective objective, Event eve) {
		if(eve instanceof EntityChangeBlockEvent) {
			EntityChangeBlockEvent event = (EntityChangeBlockEvent)eve;
			Location loc = event.getBlock().getLocation().subtract(objective.getLocation().getLocation());
			if(loc.getBlockX() == protectX && loc.getBlockZ() == protectZ) event.setCancelled(true);
		}
	}
	
}
