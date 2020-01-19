package com.kabryxis.spiritcraft.game.inventory.custom;

import org.bukkit.inventory.ItemStack;

public interface ItemStackTracker {
	
	void track(ItemStack item);
	
	void untrack(ItemStack item);
	
	void untrackAll();
	
}
