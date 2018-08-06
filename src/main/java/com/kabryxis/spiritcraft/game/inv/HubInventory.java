package com.kabryxis.spiritcraft.game.inv;

import org.bukkit.inventory.ItemStack;

public class HubInventory extends DynamicInventory {
	
	public HubInventory(ItemManager manager, String name, int rows) {
		super(manager, name, rows);
	}
	
	public void set(int index, ClickAction action, ItemStack itemStack) {
		set(index, action);
		bukkitInventory.setItem(index, itemStack);
	}
	
	public void set(int index, DynamicInventory inventory, ItemStack itemStack) {
		set(index, new OpenNextInventoryAction(inventory), itemStack);
		inventory.setPrevious(this);
	}
	
}
