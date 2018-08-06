package com.kabryxis.spiritcraft.game.inv;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface PerPlayerItemModifier {
	
	void modifyItems(SpiritPlayer player, ItemStack[] itemStacks, int trueSize);
	
}
