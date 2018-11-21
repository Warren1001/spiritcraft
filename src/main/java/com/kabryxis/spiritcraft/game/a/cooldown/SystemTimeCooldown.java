package com.kabryxis.spiritcraft.game.a.cooldown;

public class SystemTimeCooldown implements Cooldown {
	
	private long duration;
	private long stopTime;
	
	@Override
	public void start(int tickDuration) {
		this.duration = tickDuration * 50;
		stopTime = System.currentTimeMillis();
	}
	
	@Override
	public boolean isActive() {
		return System.currentTimeMillis() - stopTime <= duration;
	}
	
}
