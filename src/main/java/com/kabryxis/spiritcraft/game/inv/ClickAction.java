package com.kabryxis.spiritcraft.game.inv;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

@FunctionalInterface
public interface ClickAction {
	
	void click(SpiritPlayer player, boolean right, boolean shift);
	
}
