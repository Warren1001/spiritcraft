package com.kabryxis.spiritcraft.game.inv.utility;

import com.kabryxis.spiritcraft.game.inv.DynamicInventory;
import com.kabryxis.spiritcraft.game.inv.UtilityClickAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreviousInventoryAction implements UtilityClickAction {
	
	private final ItemStack itemStack = ItemBuilder.newItemBuilder(Material.BARRIER).name(ChatColor.GOLD + "Return to").build();
	
	@Override
	public void click(SpiritPlayer player, boolean right, boolean shift) {
		DynamicInventory current = player.getCurrent();
		boolean currentHasPrevious = current.hasPrevious();
		boolean playerHasPrevious = player.hasPrevious();
		if(currentHasPrevious && playerHasPrevious) player.openInventory(right ? player.getPrevious() : current.getPrevious());
		else if(currentHasPrevious) player.openInventory(current.getPrevious());
		else if(playerHasPrevious) player.openInventory(player.getPrevious());
	}
	
	@Override
	public void modify(SpiritPlayer player, ItemStack itemStack) {
		DynamicInventory current = player.getCurrent();
		boolean currentHasPrevious = current.hasPrevious();
		if(currentHasPrevious && current.getPrevious() == current) currentHasPrevious = false;
		boolean playerHasPrevious = player.hasPrevious();
		if(playerHasPrevious && player.getPrevious() == current) playerHasPrevious = false;
		if(currentHasPrevious || playerHasPrevious) {
			List<String> lore;
			if(currentHasPrevious && playerHasPrevious) {
				DynamicInventory currentPrevious = current.getPrevious();
				DynamicInventory playerPrevious = player.getPrevious();
				if(currentPrevious == playerPrevious) lore = Collections.singletonList(current.getPrevious().getTitle());
				else {
					lore = new ArrayList<>(2);
					lore.add(ChatColor.GOLD + "Left: " + current.getPrevious().getTitle());
					lore.add(ChatColor.GOLD + "Right: " + player.getPrevious().getTitle());
				}
			}
			else if(currentHasPrevious) lore = Collections.singletonList(current.getPrevious().getTitle());
			else lore = Collections.singletonList(player.getPrevious().getTitle());
			ItemMeta meta = itemStack.getItemMeta();
			meta.setLore(lore);
			itemStack.setItemMeta(meta);
		}
		else itemStack.setType(Material.AIR);
	}
	
	@Override
	public ItemStack getItemStack() {
		return itemStack;
	}
	
}
