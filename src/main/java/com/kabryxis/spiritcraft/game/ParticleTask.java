package com.kabryxis.spiritcraft.game;

import com.kabryxis.kabutils.spigot.concurrent.TickingBukkitRunnable;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class ParticleTask extends TickingBukkitRunnable {
	
	private final int maxSkipTicks = 400;
	private final int nonSkipTicks = (int)(maxSkipTicks - (maxSkipTicks / 10.0));
	private final SpiritPlayer player;
	
	private long defaultDelay = 1500L;
	private long baseDelay = defaultDelay;
	private long delay = defaultDelay;
	private int skippedTicks = 0;
	private long lastTick = 0L;
	private boolean skipTick = false;
	
	public ParticleTask(SpiritPlayer player) {
		this.player = player;
	}
	
	public long getDefaultDelay() {
		return defaultDelay;
	}
	
	public void setDelay(long delay) {
		this.baseDelay = delay;
	}
	
	public long getDelay() {
		return baseDelay;
	}
	
	public boolean setSkipTick(boolean skipTick) {
		this.skipTick = skipTick && skippedTicks >= nonSkipTicks;
		return this.skipTick;
	}
	
	public boolean isSkipTick() {
		return skipTick;
	}
	
	@Override
	public void tick(int tick) {
		if(skipTick) {
			skippedTicks += 2;
			if(skippedTicks >= maxSkipTicks) setSkipTick(false);
		}
		else {
			boolean setBase = false;
			if(skippedTicks > 0) {
				delay = (long)(baseDelay * (Math.max(0.2, (maxSkipTicks - skippedTicks) / (double)maxSkipTicks)));
				skippedTicks--;
				if(skippedTicks == 0) setBase = true;
			}
			long current = System.currentTimeMillis();
			if(current - lastTick >= delay) {
				lastTick = current;
				player.getParticleData().display(player);
			}
			if(setBase) delay = baseDelay;
		}
	}
	
	public BukkitTask start() {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20000000, 0, false, false)); // TODO move elsewhere
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20000000, 0, false, false)); // TODO move elsewhere
		return runTaskTimer(player.getGame().getPlugin(), 0L, 1L);
	}
	
}
