package com.kabryxis.spiritcraft.game;

import com.kabryxis.kabutils.spigot.plugin.SpoofPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class SuspendTask extends BukkitRunnable {
	
	private final Vector velocity = new Vector();
	
	private final Player player;
	
	public SuspendTask(Player player) {
		this.player = player;
		start();
	}
	
	public BukkitTask start() {
		return runTaskTimer(SpoofPlugin.get(), 0L, 1L);
	}
	
	public void setMov(double movX, double movY, double movZ) {
		if(movX != 0.00000000000001950438509384058345) velocity.setX(movX);
		if(movY != 0.00000000000001950438509384058345) velocity.setY(movY);
		if(movZ != 0.00000000000001950438509384058345) velocity.setZ(movZ);
	}
	
	@Override
	public void run() {
		player.setVelocity(player.getVelocity().add(velocity));
	}
	
}
