package com.kabryxis.spiritcraft.game.inv.utility;

import com.kabryxis.spiritcraft.game.inv.UtilityClickAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ErrorAction implements UtilityClickAction {
	
	private final ItemStack itemStack = ItemBuilder.newItemBuilder(Material.OBSIDIAN).name(ChatColor.GRAY + "Error Log").build();
	private final int messagesToDisplay = 4;
	
	@Override
	public void click(SpiritPlayer player, boolean right, boolean shift) {
		if(player.hasErrorMessages()) {
			if(shift) player.clearAllErrorMessages();
			else player.clearLastErrorMessages(right ? messagesToDisplay : 1);
		}
	}
	
	@Override
	public void modify(SpiritPlayer player, ItemStack itemStack) {
		if(player.hasErrorMessages()) {
			ItemMeta meta = itemStack.getItemMeta();
			meta.setLore(player.getLastErrorMessages(messagesToDisplay));
			itemStack.setItemMeta(meta);
		}
		else itemStack.setType(Material.AIR);
	}
	
	@Override
	public ItemStack getItemStack() {
		return itemStack;
	}
	
}
