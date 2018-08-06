package com.kabryxis.spiritcraft.game.a.world.sound;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public interface SoundPlayer {
	
	String getName();
	
	void playSound(SpiritPlayer causer);
	
}
