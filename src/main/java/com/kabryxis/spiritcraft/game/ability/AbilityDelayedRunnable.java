package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbilityDelayedRunnable implements Runnable {
	
	private final SpiritGame game;
	
	private BukkitTask currentTask;
	
	public AbilityDelayedRunnable(SpiritGame game) {
		this.game = game;
	}
	
	public void start(long delay) {
		currentTask = game.getTaskManager().start(this, delay);
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
