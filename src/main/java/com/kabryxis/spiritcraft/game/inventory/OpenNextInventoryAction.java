package com.kabryxis.spiritcraft.game.inventory;

import org.bukkit.entity.Player;

public class OpenNextInventoryAction implements InteractableItem {
	
	private final DynamicInventory attached;
	
	public OpenNextInventoryAction(DynamicInventory attached) {
		this.attached = attached;
	}
	
	@Override
	public boolean click(Player player, boolean right, boolean shift) {
		attached.open(player);
		return true;
	}
	
}
