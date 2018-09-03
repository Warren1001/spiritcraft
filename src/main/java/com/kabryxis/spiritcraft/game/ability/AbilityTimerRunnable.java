package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbilityTimerRunnable implements Runnable {
	
	private BukkitTask currentTask;
	
	public void start(long delay, long interval) {
		currentTask = BukkitThreads.syncTimer(this, delay, interval);
		onStart();
	}
	
	public void onStart() {}
	
	public boolean isRunning() {
		return currentTask != null;
	}
	
	@Override
	public void run() {
		tick();
	}
	
	public abstract void tick();
	
	public void stop() {
		currentTask.cancel();
		currentTask = null;
		onStop();
	}
	
	public void onStop() {}
	
}
