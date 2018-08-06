package com.kabryxis.spiritcraft.game;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ParticleTask extends BukkitRunnable {
	
	private final SpiritPlayer player;
	
	private long defaultDelay = 1500L;
	private long delay = 1500L;
	private int skippedTicks = 0;
	private long lastTick = 0L;
	private boolean skipTick = false;
	
	public ParticleTask(SpiritPlayer player) {
		this.player = player;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	public void setDefaultDelay() {
		this.delay = defaultDelay;
	}
	
	public long getDelay() {
		return delay;
	}
	
	public void setSkipTick(boolean skipTick) {
		if(!skipTick && skippedTicks > 0) delay /= 2;
		this.skipTick = skipTick;
	}
	
	public boolean isSkipTick() {
		return skipTick;
	}
	
	@Override
	public void run() {
		long current = System.currentTimeMillis();
		if(current - lastTick >= delay) {
			lastTick = current;
			if(skipTick) {
				skippedTicks++;
				if(skippedTicks >= 20) setSkipTick(false);
				return;
			}
			if(skippedTicks > 0) {
				skippedTicks--;
				if(skippedTicks == 0) delay *= 2;
			}
			player.getParticleData().display(player);
		}
	}
	
	public BukkitTask start() {
		return runTaskTimer(player.getGame().getPlugin(), 5L, 5L);
	}
	
}
