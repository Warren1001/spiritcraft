package com.kabryxis.spiritcraft.game.inv;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.inventory.ItemStack;

public class PerPlayerInventory extends DynamicInventory {
	
	private final PerPlayerItemModifier itemModifier;
	
	public PerPlayerInventory(ItemManager manager, String name, int rows, PerPlayerItemModifier itemModifier) {
		super(manager, name, rows);
		this.itemModifier = itemModifier;
	}
	
	@Override
	public void modifyItems(ItemStack[] items, SpiritPlayer player) {
		super.modifyItems(items, player);
		itemModifier.modifyItems(player, items, items.length - 9);
	}
	
}
