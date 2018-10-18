package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.spiritcraft.game.a.game.Game;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbilityTimerRunnable implements Runnable {
	
	private final Game game;
	
	private BukkitTask currentTask;
	
	public AbilityTimerRunnable(Game game) {
		this.game = game;
	}
	
	public void start(long delay, long interval) {
		currentTask = game.getTaskManager().start(this, delay, interval);
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
