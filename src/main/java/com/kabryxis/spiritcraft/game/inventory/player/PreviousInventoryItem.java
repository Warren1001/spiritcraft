package com.kabryxis.spiritcraft.game.inventory.player;

import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.spiritcraft.game.inventory.DynamicInventory;
import com.kabryxis.spiritcraft.game.inventory.InteractablePlayerItem;
import com.kabryxis.spiritcraft.game.inventory.InventoryManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreviousInventoryItem implements InteractablePlayerItem {
	
	private final ItemBuilder itemBase = new ItemBuilder(Material.BARRIER).name(ChatColor.GOLD + "Return to");
	
	private final InventoryManager inventoryManager;
	
	public PreviousInventoryItem(InventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}
	
	@Override
	public boolean click(Player player, boolean right, boolean shift) {
		DynamicInventory currentPrevious = inventoryManager.getCurrentlyOpen(player).getPrevious();
		DynamicInventory playerPrevious = inventoryManager.getPreviouslyOpen(player);
		boolean currentHasPrevious = currentPrevious != null;
		boolean playerHasPrevious = playerPrevious != null;
		if(currentHasPrevious && playerHasPrevious) (right ? playerPrevious : currentPrevious).open(player);
		else if(currentHasPrevious) currentPrevious.open(player);
		else if(playerHasPrevious) playerPrevious.open(player);
		return true;
	}
	
	@Override
	public ItemStack modify(Player player, ItemStack itemStack) {
		DynamicInventory current = inventoryManager.getCurrentlyOpen(player);
		DynamicInventory currentPrevious = current.getPrevious();
		DynamicInventory playerPrevious = inventoryManager.getPreviouslyOpen(player);
		boolean currentHasPrevious = currentPrevious != null;
		boolean playerHasPrevious = playerPrevious != null;
		if(playerHasPrevious && playerPrevious == current) playerHasPrevious = false;
		if(currentHasPrevious || playerHasPrevious) {
			List<String> lore;
			if(currentHasPrevious && playerHasPrevious) {
				if(currentPrevious == playerPrevious) lore = Collections.singletonList(currentPrevious.getTitle());
				else {
					lore = new ArrayList<>(2);
					lore.add(ChatColor.GOLD + "Left: " + currentPrevious.getTitle());
					lore.add(ChatColor.GOLD + "Right: " + playerPrevious.getTitle());
				}
			}
			else if(currentHasPrevious) lore = Collections.singletonList(currentPrevious.getTitle());
			else lore = Collections.singletonList(playerPrevious.getTitle());
			return itemBase.clone().lore(lore).build();
		}
		return null;
	}
	
}
