package com.kabryxis.spiritcraft.game.ability;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class ExplodeStructureTask extends BukkitRunnable {
	
	private final Plugin plugin;
	private final Vector min, max;
	
	public ExplodeStructureTask(Plugin plugin, Vector min, Vector max) {
		this.plugin = plugin;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public void run() {
	
	}
	
	public BukkitTask start() {
		return runTaskTimer(plugin, 0L, 1L);
	}
	
}
