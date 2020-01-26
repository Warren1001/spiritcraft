package com.kabryxis.spiritcraft.game.inventory;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.kabutils.spigot.listener.Listeners;
import com.kabryxis.kabutils.spigot.plugin.protocollibrary.BasicSendingPacketAdapter;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Function;

public class InventoryManager implements Listener {
	
	public final static ItemStack ERROR_ITEMSTACK = new ItemBuilder(Material.OBSIDIAN).name(ChatColor.GRAY + "Error Log").lore("${errors}").build();
	public final static ItemStack INFO_ITEMSTACK = new ItemBuilder(Material.SIGN).name(ChatColor.AQUA + "Information")
			.lore(ChatColor.GOLD + "Money: ${curr}", ChatColor.GOLD + "Inventory Space: ${inv_space}").build();
	public final static ItemStack PREV_INV_ITEMSTACK = new ItemBuilder(Material.BARRIER).name(ChatColor.GOLD + "Return to")
			.lore("${prev_inv}").build();
	
	private final Map<Player, Inventory> inventoryHistory = new HashMap<>();
	private final Map<String, Function<Player, String>> replacableKeys = new HashMap<>();
	
	private final SpiritGame game;
	
	public InventoryManager(SpiritGame game) {
		this.game = game;
		Plugin plugin = game.getPlugin();
		Listeners.registerListener(this, plugin);
		ProtocolLibrary.getProtocolManager().addPacketListener(new BasicSendingPacketAdapter(plugin, event -> {
			Player player = event.getPlayer();
			Inventory inventory = player.getOpenInventory().getTopInventory();
			if(inventory.getType() == InventoryType.CHEST) {
				PacketContainer packet = event.getPacket();
				List<ItemStack> items = packet.getItemListModifier().read(0);
				items.forEach(item -> {
					if(item.hasItemMeta() && item.getItemMeta().hasLore()) {
						ItemMeta meta = item.getItemMeta();
						List<String> lore = meta.getLore();
						List<String> newLore = new ArrayList<>(lore.size());
						for(String line : lore) {
							for(Map.Entry<String, Function<Player, String>> entry : replacableKeys.entrySet()) {
								String key = entry.getKey();
								Function<Player, String> function = entry.getValue();
								if(line.contains(key)) line = line.replace(String.format("${%s}", key), function.apply(player));
							}
							if(line.contains("\n")) newLore.addAll(Arrays.asList(line.split("\n")));
							else newLore.add(line);
						}
						item.setItemMeta(meta);
					}
				});
				packet.getItemListModifier().write(0, items);
			}
		}, PacketType.Play.Server.WINDOW_ITEMS));
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		ItemStack item = event.getView().getItem(event.getRawSlot());
		if(item instanceof ActionItemStack) {
			((ActionItemStack)item).clicked((Player)event.getWhoClicked());
			event.setCancelled(true);
		}
	}
	
	/*public void updateInventory(Player player) {
		game.getTaskManager().start(player::updateInventory);
	}*/
	
	/*@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryDrag(InventoryDragEvent event) {
		Player player = (Player)event.getWhoClicked();
		DynamicInventory inventory = currentlyOpen.get(player);
		if(inventory != null) {
			for(int rawSlot : event.getRawSlots()) {
				if(inventory.onClick(player, rawSlot, rawSlot, event.getType() == DragType.SINGLE, false, true)) {
					event.setCancelled(true);
					updateInventory(player);
					break;
				}
			}
		}
	}*/
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Inventory inv = event.getInventory();
		if(inv.getType() == InventoryType.CHEST) inventoryHistory.put((Player)event.getPlayer(), inv);
	}
	
}
