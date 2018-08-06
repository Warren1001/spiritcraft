package com.kabryxis.spiritcraft.game.inv;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.kabutils.spigot.version.Version;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class PlayerConfigureInventory extends DynamicInventory { // TODO opening another inventory in an inventory does not call the close event, needs to be called manually
	
	private static final ItemStack FILL = ItemBuilder.newItemBuilder(Material.BARRIER).name(ChatColor.BLACK.toString()).build();
	
	private final boolean ghost;
	
	public PlayerConfigureInventory(ItemManager manager, String name, boolean ghost) {
		super(manager, name, 5);
		this.ghost = ghost;
		for(int i = (Version.VERSION.isVersionAtLeast(Version.v1_9_R1) ? 5 : 4); i < 9; i++) {
			bukkitInventory.setItem(i, FILL);
		}
		setUtility(0, manager.getPreviousInventoryAction());
		setUtility(4, manager.getInformationAction());
	}
	
	@Override
	public void open(SpiritPlayer player) {
		ItemStack[] contents = bukkitInventory.getContents();
		player.getPlayerItemInfo(ghost).populateSelectedConfiguration(contents);
		bukkitInventory.setContents(contents);
		super.open(player);
	}
	
	@EventHandler
	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player)event.getWhoClicked();
		InventoryView view = player.getOpenInventory();
		if(!view.getTopInventory().getTitle().equals(bukkitInventory.getTitle())) return;
		Inventory clicked = event.getClickedInventory();
		if(clicked == null || !clicked.getTitle().equals(bukkitInventory.getTitle())) {
			event.setCancelled(true);
			return;
		}
		int slot = event.getSlot();
		if(slot < 0 || slot >= bukkitInventory.getSize()) return;
		if(slot == 4) {
			if(!Version.VERSION.isVersionAtLeast(Version.v1_9_R1)) {
				event.setCancelled(true);
				return;
			}
		}
		else if(slot > 4 && slot < 9) {
			event.setCancelled(true);
			return;
		}
		else if(slot >= 45) event.setCancelled(true);
		ClickAction action = clickActions[slot];
		if(action != null) {
			player.updateInventory();
			action.click(manager.getGame().getPlayerManager().getPlayer(player), event.getClick().isRightClick(), event.getClick().isShiftClick());
			BukkitThreads.sync(player::updateInventory);
			// return;
		}
	}
	
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		Player player = (Player)event.getWhoClicked();
		InventoryView view = player.getOpenInventory();
		if(!view.getTopInventory().getTitle().equals(bukkitInventory.getTitle())) return;
		for(int slot : event.getRawSlots()) {
			if(slot == 4) {
				if(!Version.VERSION.isVersionAtLeast(Version.v1_9_R1)) {
					event.setCancelled(true);
					return;
				}
			}
			else if(slot > 4 && slot < 9) {
				event.setCancelled(true);
				return;
			}
			if(slot >= 45) event.setCancelled(true);
		}
	}
	
	@Override
	public void onClose(SpiritPlayer player) {
		ItemStack[] contents = bukkitInventory.getContents();
		player.getPlayerItemInfo(ghost).saveSelectedConfiguration(contents);
		for(int i = 0; i < 45; i++) {
			if(i >= (Version.VERSION.isVersionAtLeast(Version.v1_9_R1) ? 5 : 4) && i < 9) continue;
			contents[i] = null;
		}
		bukkitInventory.setContents(contents);
	}
	
	
	
}
