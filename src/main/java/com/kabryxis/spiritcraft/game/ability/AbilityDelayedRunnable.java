package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.spiritcraft.game.a.game.Game;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbilityDelayedRunnable implements Runnable {
	
	private final Game game;
	
	private BukkitTask currentTask;
	
	public AbilityDelayedRunnable(Game game) {
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
