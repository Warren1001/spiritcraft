package com.kabryxis.spiritcraft.game.inventory;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.kabryxis.kabutils.spigot.listener.Listeners;
import com.kabryxis.kabutils.spigot.plugin.protocollibrary.BasicSendingPacketAdapter;
import com.kabryxis.kabutils.spigot.version.Version;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.inventory.player.ErrorItem;
import com.kabryxis.spiritcraft.game.inventory.player.InformationItem;
import com.kabryxis.spiritcraft.game.inventory.player.PreviousInventoryItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InventoryManager implements Listener {
	
	private final Map<Player, DynamicInventory> currentlyOpen = new HashMap<>();
	private final Map<Player, DynamicInventory> previouslyOpen = new HashMap<>();
	
	private final SpiritGame game;
	private final PreviousInventoryItem previousInventoryItem;
	private final InformationItem informationItem;
	private final ErrorItem errorItem;
	
	public InventoryManager(SpiritGame game) {
		this.game = game;
		previousInventoryItem = new PreviousInventoryItem(this);
		informationItem = new InformationItem(game);
		errorItem = new ErrorItem(game);
		Plugin plugin = game.getPlugin();
		Listeners.registerListener(this, plugin);
		ProtocolLibrary.getProtocolManager().addPacketListener(new BasicSendingPacketAdapter(plugin, event -> {
			Player player = event.getPlayer();
			DynamicInventory inventory = getCurrentlyOpen(player);
			if(inventory != null) {
				PacketContainer packet = event.getPacket();
				ItemStack[] items = getFromModifier(packet);
				inventory.constructPlayerItems(player, items);
				writeToModifier(packet, items);
			}
		}, PacketType.Play.Server.WINDOW_ITEMS));
	}
	
	public DynamicInventory getCurrentlyOpen(Player player) {
		return currentlyOpen.get(player);
	}
	
	private ItemStack[] getFromModifier(PacketContainer packet) {
		return Version.VERSION.isVersionAtLeast(Version.v1_11_R1) ? packet.getItemListModifier().read(0).toArray(new ItemStack[0]) : packet.getItemArrayModifier().read(0);
	}
	
	private void writeToModifier(PacketContainer packet, ItemStack[] items) {
		if(Version.VERSION.isVersionAtLeast(Version.v1_11_R1)) packet.getItemListModifier().write(0, Arrays.asList(items));
		else packet.getItemArrayModifier().write(0, items);
	}
	
	public PreviousInventoryItem getPreviousInventoryItem() {
		return previousInventoryItem;
	}
	
	public InformationItem getInformationItem() {
		return informationItem;
	}
	
	public ErrorItem getErrorItem() {
		return errorItem;
	}
	
	public DynamicInventory getPreviouslyOpen(Player player) {
		return previouslyOpen.get(player);
	}
	
	public void inventoryOpenedByPlayer(Player player, DynamicInventory inventory) {
		currentlyOpen.put(player, inventory);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player)event.getWhoClicked();
		DynamicInventory inventory = currentlyOpen.get(player);
		int rawSlot = event.getRawSlot();
		if(inventory != null && rawSlot >= 0 && inventory.onClick(player, event.getSlot(), rawSlot, event.getClick().isRightClick(), event.getClick().isShiftClick(), false)) {
			event.setCancelled(true);
			updateInventory(player);
		}
	}
	
	public void updateInventory(Player player) {
		game.getTaskManager().start(player::updateInventory);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		inventoryClosedByPlayer((Player)event.getPlayer());
	}
	
	public void inventoryClosedByPlayer(Player player) {
		DynamicInventory inv = currentlyOpen.remove(player);
		if(inv != null) {
			previouslyOpen.put(player, inv);
			inv.onClose(player);
		}
	}
	
}
