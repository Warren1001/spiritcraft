package com.kabryxis.spiritcraft.game.a.tracker;

import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemTracker implements Consumer<Collection<ItemStack>> {
	
	private final Map<String, ItemTrackerSet> itemTrackerSets = new HashMap<>();
	
	private final SpiritPlayer player;
	private final SpiritGame game;
	
	public ItemTracker(SpiritPlayer player) {
		this.player = player;
		this.game = player.getGame();
	}
	
	public void updateReferences() {
		itemTrackerSets.values().forEach(Set::clear);
		PlayerInventory inventory = player.getInventory();
		for(int i = 0; i < inventory.getSize() + 4; i++) {
			ItemStack item = inventory.getItem(i); // #getItem may be required for craftmirror instance
			if(Items.exists(item)) itemTrackerSets.values().forEach(set -> set.add(item));
		}
	}
	
	@Override
	public void accept(Collection<ItemStack> items) {
		itemTrackerSets.values().forEach(set -> {
			set.clear();
			set.addAll(items);
		});
	}
	
	public Collection<ItemStack> track(String name, Predicate<ItemStack> itemTester) {
		return itemTrackerSets.computeIfAbsent(name, ignore -> {
			ItemTrackerSet set = new ItemTrackerSet(itemTester);
			PlayerInventory inventory = player.getInventory();
			for(int i = 0; i < inventory.getSize() + 4; i++) {
				ItemStack item = inventory.getItem(i); // #getItem may be required for craftmirror instance
				if(Items.exists(item)) set.add(item);
			}
			return set;
		});
	}
	
}
