package com.kabryxis.spiritcraft.game.a.tracker;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Random;

public class ItemTrackerTest implements Runnable {
	
	private final Collection<ItemStack> items;
	
	public ItemTrackerTest(Collection<ItemStack> items) {
		this.items = items;
	}
	
	@Override
	public void run() {
		items.forEach(item -> item.setDurability((short)new Random().nextInt(item.getType().getMaxDurability())));
	}
	
}
