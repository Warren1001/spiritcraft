package com.kabryxis.spiritcraft.game.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface PlayerItem {
	
	ItemStack modify(Player player, ItemStack itemStack);
	
}
