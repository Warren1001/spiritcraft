package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.data.Arrays;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WorldEndTask extends BukkitRunnable {
	
	private static final double MAX_DISTANCE_SQUARED = 18.973665961010276;
	private static final int RADIUS = 14;
	
	private final FloatingBlockSet[] floatingBlocks = new FloatingBlockSet[360];
	private final Set<TickingBlock> tickingBlocks = new HashSet<>();
	
	private final SpiritGame game;
	private final Location center;
	
	/**
	 * tick 360 is the end of Sound.ENDERDRAGON_DEATH cycle
	 */
	private int tick = 0;
	
	public WorldEndTask(SpiritGame game, Location center) {
		this.game = game;
		this.center = center;
		start();
	}
	
	public BukkitTask start() {
		Random rand = new Random();
		for(int x = -RADIUS; x <= RADIUS; x++) {
			for(int y = -RADIUS; y <= RADIUS; y++) {
				for(int z = -RADIUS; z <= RADIUS; z++) {
					Block block = center.getBlock().getRelative(x, y, z);
					if(block.getType().isSolid()) {
						double dist = block.getLocation().distanceSquared(center);
						if(dist > 360.0) continue;
						int index = (int)(dist * (360.0 / (RADIUS * RADIUS))) + (rand.nextInt(21) - 10);
						if(index < 0) index = 0;
						else if(index > 359) index = 359;
						if(rand.nextInt(400) + 1 > Math.max(8, 360.0 / Math.max(1, (index + 1) / 120.0))) continue;
						Arrays.computeIfAbsent(floatingBlocks, index, FloatingBlockSet::new).add(rand.nextInt(4) <= 1 ? new TickingBlock(center, block) : new FloatingBlock(block));
					}
				}
			}
		}
		//center.getWorld().playEffect(center, Effect.EXPLOSION_HUGE, 100);
		center.getWorld().playSound(center, Sound.ENTITY_ENDER_DRAGON_DEATH, 10F, 0.1F);
		return runTaskTimer(game.getPlugin(), 0L, 1L);
	}
	
	@Override
	public void run() {
		if(tick == 420) {
			cancel();
			return;
		}
		if(tick < 360) {
			FloatingBlockSet blockSet = floatingBlocks[tick];
			if(blockSet != null) {
				blockSet.forEach(block -> {
					block.start();
					if(block instanceof TickingBlock) tickingBlocks.add((TickingBlock)block);
				});
			}
			tickingBlocks.forEach(TickingBlock::tick);
		}
		else if(tick == 360) {
			//center.getWorld().playSound(center, Sound., 10F, 0.1F);
			center.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, center, 100);
			tickingBlocks.forEach(TickingBlock::fast);
		}
		tick++;
	}
	
	@Override
	public synchronized void cancel() throws IllegalStateException {
		super.cancel();
		tick = 0;
		tickingBlocks.clear();
		for(int i = 0; i < floatingBlocks.length; i++) {
			FloatingBlockSet blockSet = floatingBlocks[i];
			if(blockSet != null) {
				floatingBlocks[i] = null;
				blockSet.forEach(FloatingBlock::end);
			}
		}
	}
	
}
