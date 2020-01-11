package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.data.Maths;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class FireBreathTask extends BukkitRunnable {
	
	private static final int MAX_ITERATIONS = 250;
	private static final int EXPLODE_ITERATION = 40;
	
	private final SpiritPlayer owner;
	
	private int iteration = 0;
	private Location loc;
	private Vector direction;
	
	public FireBreathTask(SpiritPlayer owner) {
		this.owner = owner;
		start();
	}
	
	@Override
	public void run() {
		if(iteration <= EXPLODE_ITERATION) {
			Location temp = loc.add(direction);
			if(temp.getBlock().getType() != Material.AIR) iteration = EXPLODE_ITERATION + 1;
			else loc = temp;
		}
		double offset;
		if(iteration <= EXPLODE_ITERATION) offset = (iteration / (double)EXPLODE_ITERATION) / 2.1;
		else offset = ((iteration - (EXPLODE_ITERATION + 1) + iteration / (double)EXPLODE_ITERATION) / 8.0) + 1;
		if(offset > 3.0) offset = 3.0;
		double particleCount = Maths.floor(offset * 50);
		if(iteration > 210) particleCount /= ((iteration - 210) / 5.0) + 1;
		//System.out.println(iteration + ":" + offset + "," + particleCount);
		if(iteration <= EXPLODE_ITERATION || iteration % 2 == 0) {
			for(Player player : owner.getPlayer().getWorld().getPlayers()) {
				player.spawnParticle(Particle.FLAME, loc, (int)particleCount, offset, offset / 1.5, offset);
			}
		}
		iteration++;
		if(iteration == MAX_ITERATIONS) cancel();
	}
	
	public BukkitTask start() {
		direction = owner.getPlayer().getLocation().getDirection().multiply(0.25);
		loc = owner.getPlayer().getEyeLocation().subtract(0, 0.15, 0);
		return runTaskTimer(owner.getGame().getPlugin(), 0L, 1L);
	}
	
}
