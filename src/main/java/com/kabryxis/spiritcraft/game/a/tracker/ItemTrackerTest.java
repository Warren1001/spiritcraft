package com.kabryxis.spiritcraft.game.a.tracker;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Random;

public class ItemTrackerTest implements Runnable {
	
	private final Collection<ItemStack> items;
	
	public ItemTrackerTest(Collection<ItemStack> items) {
		this.items = items;
	}
	
	@Override
	public void run() {
		items.forEach(item -> {
			ItemMeta meta = item.getItemMeta();
			((Damageable)meta).setDamage(new Random().nextInt(item.getType().getMaxDurability()));
			item.setItemMeta(meta);
		});
	}
	
}
