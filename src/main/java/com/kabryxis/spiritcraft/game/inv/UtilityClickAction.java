package com.kabryxis.spiritcraft.game.inv;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.inventory.ItemStack;

public interface UtilityClickAction extends ClickAction {
	
	void modify(SpiritPlayer player, ItemStack itemStack);
	
	ItemStack getItemStack();
	
}
