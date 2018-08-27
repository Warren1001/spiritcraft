package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbilityRunnable implements Runnable {
	
	private BukkitTask currentTask;
	
	public void startRepeating(long delay, long interval) {
		if(isRunning()) return;
		onStart();
		currentTask = BukkitThreads.syncTimer(this, delay, interval);
	}
	
	public boolean isRunning() {
		return currentTask != null && Bukkit.getScheduler().isCurrentlyRunning(currentTask.getTaskId());
	}
	
	public void onStart() {}
	
	public void startSingle(long delay) {
		if(isRunning()) return;
		onStart();
		currentTask = BukkitThreads.syncLater(this, delay);
	}
	
	public void stop() {
		currentTask.cancel();
		currentTask = null;
		onStop();
	}
	
	public void onStop() {}
	
}
