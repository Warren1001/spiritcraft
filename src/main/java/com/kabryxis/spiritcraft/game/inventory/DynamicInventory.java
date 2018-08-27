package com.kabryxis.spiritcraft.game.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface DynamicInventory {
	
	String getTitle();
	
	int getSize();
	
	void setInteractableItem(int index, InteractableItem item);
	
	void setServerItem(int index, ItemStack itemStack);
	
	void setInteractableServerItem(int index, InteractableItem item, ItemStack itemStack);
	
	void setPlayerItem(int index, PlayerItem item);
	
	void setInteractablePlayerItem(int index, InteractableItem item, PlayerItem playerItem);
	
	void setInteractablePlayerItem(int index, InteractablePlayerItem item);
	
	boolean hasPrevious();
	
	DynamicInventory getPrevious();
	
	void setPrevious(DynamicInventory previous);
	
	void open(Player player);
	
	void onClose(Player player);
	
	boolean onClick(Player player, int slot, int rawSlot, boolean right, boolean shift, boolean drag);
	
	void constructPlayerItems(Player player, ItemStack[] items);
	
}
