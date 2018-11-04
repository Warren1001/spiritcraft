package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbilityTimerRunnable implements Runnable {
	
	private final SpiritGame game;
	
	private BukkitTask currentTask;
	
	public AbilityTimerRunnable(SpiritGame game) {
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
