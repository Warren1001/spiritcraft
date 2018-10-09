package com.kabryxis.spiritcraft.game.a.tracker;

import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.function.Predicate;

public class ItemTrackerSet extends HashSet<ItemStack> {
	
	private final Predicate<ItemStack> predicate;
	
	public ItemTrackerSet(Predicate<ItemStack> predicate) {
		this.predicate = predicate;
	}
	
	@Override
	public boolean add(ItemStack item) {
		return predicate.test(item) && super.add(item);
	}
}
