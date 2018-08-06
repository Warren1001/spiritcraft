package com.kabryxis.spiritcraft.game.inv;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public class OpenNextInventoryAction implements ClickAction {
	
	private final DynamicInventory attached;
	
	public OpenNextInventoryAction(DynamicInventory attached) {
		this.attached = attached;
	}
	
	@Override
	public void click(SpiritPlayer player, boolean right, boolean shift) {
		player.openInventory(attached);
	}
	
}
