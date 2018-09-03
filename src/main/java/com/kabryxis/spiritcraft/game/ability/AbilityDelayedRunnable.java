package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbilityDelayedRunnable implements Runnable {
	
	private BukkitTask currentTask;
	
	public void start(long delay) {
		currentTask = BukkitThreads.syncLater(this, delay);
		onStart();
	}
	
	public void onStart() {}
	
	public boolean isRunning() {
		return currentTask != null;
	}
	
	@Override
	public void run() {
		execute();
		currentTask = null;
		onStop();
	}
	
	public abstract void execute();
	
	public void stop() {
		currentTask.cancel();
		currentTask = null;
		onStop();
	}
	
	public void onStop() {}
	
}
