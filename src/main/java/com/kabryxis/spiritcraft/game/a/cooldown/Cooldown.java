package com.kabryxis.spiritcraft.game.a.cooldown;

public interface Cooldown {
	
	void start(int tickDuration);
	
	boolean isActive();
	
}
