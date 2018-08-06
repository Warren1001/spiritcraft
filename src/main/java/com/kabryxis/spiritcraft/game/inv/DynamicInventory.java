package com.kabryxis.spiritcraft.game.inv;

import com.kabryxis.spiritcraft.Spiritcraft;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DynamicInventory implements Listener {
	
	protected final UtilityClickAction[] utilityClickActions = new UtilityClickAction[9];
	
	protected final ItemManager manager;
	protected final int trueSize;
	protected final Inventory bukkitInventory;
	protected final ClickAction[] clickActions;
	
	private DynamicInventory previous;
	
	public DynamicInventory(ItemManager manager, String name, int rows) {
		Validate.isTrue(rows > 0 && rows < 6, "amount of rows must be exclusively between 0 and 6");
		this.manager = manager;
		this.trueSize = rows * 9;
		int size = trueSize + 9;
		Spiritcraft plugin = manager.getGame().getPlugin();
		Server server = plugin.getServer();
		this.bukkitInventory = server.createInventory(null, size, name);
		this.clickActions = new ClickAction[size];
		server.getPluginManager().registerEvents(this, plugin);
	}
	
	public String getTitle() {
		return bukkitInventory.getTitle();
	}
	
	public void set(int index, ClickAction action) {
		Validate.isTrue(index >= 0 && index < trueSize, "index must be inclusively between 0 and " + (trueSize - 1));
		clickActions[index] = action;
	}
	
	public void setUtility(int index, UtilityClickAction action) {
		Validate.isTrue(index >= 0 && index < 9, "index must be inclusively between 0 and 8");
		utilityClickActions[index] = action;
		int trueIndex = trueSize + index;
		clickActions[trueIndex] = action;
		bukkitInventory.setItem(trueIndex, action.getItemStack());
	}
	
	public void open(SpiritPlayer player) {
		player.getPlayer().openInventory(bukkitInventory);
		BukkitThreads.sync(player.getPlayer()::updateInventory);
	}
	
	public void onClose(SpiritPlayer player) {}
	
	public void modifyItems(ItemStack[] items, SpiritPlayer player) {
		for(int i = trueSize; i < bukkitInventory.getSize(); i++) {
			UtilityClickAction action = utilityClickActions[i - trueSize];
			if(action != null) action.modify(player, items[i]);
		}
	}
	
	public void setPrevious(DynamicInventory previous) {
		this.previous = previous;
	}
	
	public boolean hasPrevious() {
		return previous != null;
	}
	
	public DynamicInventory getPrevious() {
		return previous;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory clicked = event.getClickedInventory();
		if(clicked != null && clicked.getTitle().equals(bukkitInventory.getTitle())) {
			event.setCancelled(true);
			Player player = (Player)event.getWhoClicked();
			int slot = event.getSlot();
			player.updateInventory();
			if(slot >= 0 && slot < bukkitInventory.getSize()) {
				ClickAction action = clickActions[slot];
				if(action != null) {
					action.click(manager.getGame().getPlayerManager().getPlayer(player), event.getClick().isRightClick(), event.getClick().isShiftClick());
					BukkitThreads.sync(player::updateInventory);
				}
			}
		}
	}
	
}
