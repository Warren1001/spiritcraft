package com.kabryxis.spiritcraft.game.a.cooldown;

public interface Cooldown {
	
	void start(long duration);
	
	boolean isActive();
	
}
