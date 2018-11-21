package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.spigot.concurrent.BukkitTaskManager;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbilityTimerRunnable extends BukkitRunnable {
	
	protected final SpiritGame game;

	protected BukkitTask currentTask;
	protected int tick;
	protected int duration;
	
	public AbilityTimerRunnable(SpiritGame game) {
		this.game = game;
	}
	
	public BukkitTask start(int delay, int interval, int duration) {
		if(currentTask == null) {
			this.duration = duration;
			tick = delay;
			currentTask = game.getTaskManager().start(this, delay, interval);
			onStart();
		}
		return currentTask;
	}
	
	public void onStart() {}
	
	public boolean isRunning() {
		return currentTask != null && BukkitTaskManager.isRunning(currentTask);
	}
	
	@Override
	public void run() {
		tick(tick++);
		if(tick == duration) cancel();
	}
	
	public abstract void tick(int tick);

	@Override
	public synchronized void cancel() {
		if(currentTask != null) {
			super.cancel();
			currentTask = null;
			onStop();
		}
	}

	public void onStop() {}
	
}
