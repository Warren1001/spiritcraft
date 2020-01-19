package com.kabryxis.spiritcraft.game.a.tracker;

import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.spiritcraft.game.inventory.custom.ItemStackTracker;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.function.Predicate;

public class ItemTracker implements ItemStackTracker {
	
	private final Map<String, Set<ItemStack>> itemTrackerSets = new HashMap<>();
	
	private final SpiritPlayer player;
	
	public ItemTracker(SpiritPlayer player) {
		this.player = player;
	}
	
	public void updateReferences() {
		untrackAll();
		PlayerInventory inventory = player.getInventory();
		for(int i = 0; i < inventory.getSize() + 4; i++) {
			ItemStack item = inventory.getItem(i);
			if(Items.exists(item)) itemTrackerSets.values().forEach(set -> set.add(item));
		}
	}
	
	@Override
	public void track(ItemStack item) {
		itemTrackerSets.values().forEach(set -> set.add(item));
	}
	
	@Override
	public void untrack(ItemStack item) {
		itemTrackerSets.values().forEach(set -> set.remove(item));
	}
	
	@Override
	public void untrackAll() {
		itemTrackerSets.values().forEach(Set::clear);
	}
	
	public Collection<ItemStack> track(String name, Predicate<ItemStack> itemTester) {
		return Collections.unmodifiableSet(itemTrackerSets.computeIfAbsent(name, ignore -> {
			Set<ItemStack> set = new ItemTrackerSet(itemTester);
			PlayerInventory inventory = player.getInventory();
			for(int i = 0; i < inventory.getSize() + 4; i++) {
				ItemStack item = inventory.getItem(i);
				if(Items.exists(item)) set.add(item);
			}
			return set;
		}));
	}
	
}
