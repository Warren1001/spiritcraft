package com.kabryxis.spiritcraft.game.inventory;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SpiritInventory implements DynamicInventory {
	
	protected final InventoryManager inventoryManager;
	protected final boolean disablePersonalInventoryInteraction;
	protected final Inventory bukkitInventory;
	protected final InteractableItem[] interactableItems;
	protected final PlayerItem[] playerItems;
	
	private DynamicInventory previous;
	
	public SpiritInventory(InventoryManager inventoryManager, String name, int rows) {
		this(inventoryManager, name, rows, true);
	}
	
	public SpiritInventory(InventoryManager inventoryManager, String name, int rows, boolean disablePersonalInventoryInteraction) {
		Validate.isTrue(rows > 0 && rows <= 6, "rows must be inclusively between 1 and 6");
		this.inventoryManager = inventoryManager;
		this.disablePersonalInventoryInteraction = disablePersonalInventoryInteraction;
		int size = rows * 9;
		bukkitInventory = Bukkit.createInventory(null, size, name);
		interactableItems = new InteractableItem[size];
		playerItems = new PlayerItem[size];
	}
	
	@Override
	public String getTitle() {
		return bukkitInventory.getTitle();
	}
	
	@Override
	public int getSize() {
		return bukkitInventory.getSize();
	}
	
	@Override
	public void setInteractableItem(int index, InteractableItem item) {
		interactableItems[index] = item;
	}
	
	@Override
	public void setServerItem(int index, ItemStack itemStack) {
		bukkitInventory.setItem(index, itemStack);
	}
	
	@Override
	public void setInteractableServerItem(int index, InteractableItem item, ItemStack itemStack) {
		setInteractableItem(index, item);
		setServerItem(index, itemStack);
	}
	
	@Override
	public void setPlayerItem(int index, PlayerItem item) {
		playerItems[index] = item;
	}
	
	@Override
	public void setInteractablePlayerItem(int index, InteractableItem item, PlayerItem playerItem) {
		setInteractableItem(index, item);
		setPlayerItem(index, playerItem);
	}
	
	@Override
	public void setInteractablePlayerItem(int index, InteractablePlayerItem item) {
		setInteractablePlayerItem(index, item, item);
	}
	
	@Override
	public boolean hasPrevious() {
		return previous != null;
	}
	
	@Override
	public DynamicInventory getPrevious() {
		return previous;
	}
	
	@Override
	public void setPrevious(DynamicInventory previous) {
		this.previous = previous;
	}
	
	@Override
	public void open(Player player) {
		player.openInventory(bukkitInventory);
		inventoryManager.inventoryOpenedByPlayer(player, this);
		inventoryManager.updateInventory(player);
	}
	
	@Override
	public void onClose(Player player) {}
	
	@Override
	public boolean onClick(Player player, int slot, int rawSlot, boolean right, boolean shift, boolean drag) {
		if(rawSlot < getSize()) {
			InteractableItem action = interactableItems[slot];
			return action != null && action.click(player, right, shift);
		}
		return disablePersonalInventoryInteraction;
	}
	
	@Override
	public void constructPlayerItems(Player player, ItemStack[] items) {
		for(int i = 0; i < getSize(); i++) {
			PlayerItem item = playerItems[i];
			if(item != null) items[i] = item.modify(player, items[i]);
		}
	}
	
}
