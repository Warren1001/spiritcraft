package com.kabryxis.spiritcraft.game.inv.utility;

import com.kabryxis.spiritcraft.game.inv.UtilityClickAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class InformationAction implements UtilityClickAction {
	
	private final ItemStack itemStack = ItemBuilder.newItemBuilder(Material.SIGN).name(ChatColor.AQUA + "Information").build();
	
	@Override
	public void click(SpiritPlayer player, boolean right, boolean shift) {}
	
	@Override
	public void modify(SpiritPlayer player, ItemStack itemStack) {
		ItemMeta meta = itemStack.getItemMeta();
		meta.setLore(Arrays.asList(ChatColor.GOLD + "Money: " + ChatColor.YELLOW + player.getCurrency(),
				ChatColor.GOLD + "Space: " + ChatColor.YELLOW + player.getItemSpace()));
		itemStack.setItemMeta(meta);
	}
	
	@Override
	public ItemStack getItemStack() {
		return itemStack;
	}
	
}
