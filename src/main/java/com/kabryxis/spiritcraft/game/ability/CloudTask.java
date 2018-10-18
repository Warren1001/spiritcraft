package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.data.NumberConversions;
import com.kabryxis.spiritcraft.game.a.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.inventivetalent.particle.ParticleEffect;

import java.util.*;

public class CloudTask extends BukkitRunnable {
	
	private static final int MAX_BLOCK_ITERATIONS = 8;
	private static final double DISTANCE = Math.pow(8, 2);
	private static final int DURATION = 6;
	private static final int INTERVAL = 1;
	private static final int ITERATIONS = NumberConversions.ceil(DURATION * (20.0 / INTERVAL));
	
	private final Game game;
	private final Location loc;
	private final List<Set<Block>> blocksList = new ArrayList<>(MAX_BLOCK_ITERATIONS);
	private final BlockFace[] faces = { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST };
	
	private Block trueOrigin;
	private int currentIteration;
	private int currentBlockIteration;
	
	public CloudTask(Game game, Location loc) {
		this.game = game;
		this.loc = loc;
		for(int i = 0; i < MAX_BLOCK_ITERATIONS; i++) {
			blocksList.add(new HashSet<>());
		}
		start();
	}
	
	private void addBlocks(Block origin, BlockFace cameFrom) {
		for(BlockFace face : faces) {
			if(face == cameFrom) continue;
			Set<Block> blocks = blocksList.get(currentBlockIteration);
			Block block = origin.getRelative(face);
			if(block.getType() != Material.AIR || blocksList.stream().anyMatch(blocks1 -> blocks1.contains(block)) ||
					block.getLocation().distanceSquared(trueOrigin.getLocation()) > DISTANCE) continue;
			blocks.add(block);
			addBlocks(block, face);
			currentBlockIteration++;
			if(currentBlockIteration == MAX_BLOCK_ITERATIONS) currentBlockIteration = 0;
		}
	}
	
	@Override
	public void run() {
		currentIteration++;
		if(currentIteration == ITERATIONS) {
			cancel();
			trueOrigin = null;
			currentIteration = 0;
			return;
		}
		Random random = new Random();
		blocksList.get(currentBlockIteration).forEach(b -> ParticleEffect.FLAME.send(loc.getWorld().getPlayers(),
				b.getLocation().subtract(0.5, 0.5, 0.5), 0.6, 0.6, 0.6, 0, 1));
		currentBlockIteration++;
		if(currentBlockIteration == MAX_BLOCK_ITERATIONS) currentBlockIteration = 0;
	}
	
	public BukkitTask start() {
		Block block = loc.getBlock();
		Set<Block> blocks = blocksList.get(0);
		blocks.add(block);
		trueOrigin = block.getRelative(BlockFace.UP);
		blocks.add(trueOrigin);
		blocks.add(block.getRelative(BlockFace.DOWN));
		Block top = trueOrigin.getRelative(BlockFace.UP);
		blocks.add(top);
		blocks.add(top.getRelative(BlockFace.UP));
		new HashSet<>(blocks).forEach(b -> addBlocks(b, null));
		currentBlockIteration = 0;
		return game.getTaskManager().start(this, 0L, INTERVAL);
	}
	
}
