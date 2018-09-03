package com.kabryxis.spiritcraft.game.item;

import com.google.common.collect.Sets;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.kabutils.spigot.version.Version;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class PlayerItemInfo {
	
	private final ItemData itemData;
	private final SpiritPlayer player;
	private final Config data;
	private final boolean ghost;
	private final String type;
	
	public PlayerItemInfo(ItemData itemData, SpiritPlayer player, boolean ghost) {
		this.itemData = itemData;
		this.player = player;
		this.data = player.getData();
		this.ghost = ghost;
		this.type = ghost ? "ghost" : "hunter";
	}
	
	public boolean owns(String item) {
		ConfigSection itemSection = data.getChild("inventory." + type);
		return itemSection != null && itemSection.getList("owns", String.class, Collections.emptyList()).contains(item);
	}
	
	public void bought(String item) {
		ConfigSection itemSection = data.getChild("inventory." + type, true);
		List<String> itemsOwned = itemSection.getList("owns", String.class, new ArrayList<>());
		itemsOwned.add(item);
		itemSection.set("owns", itemsOwned);
		data.save();
	}
	
	public boolean isSelected(String item) {
		ConfigSection selectedSection = data.getChild("inventory." + type + ".selected");
		return selectedSection != null && selectedSection.getChildren().stream().anyMatch(section -> section.get("name", String.class).equals(item));
	}
	
	public int getTotalAmount(String item) {
		ConfigSection selectedSection = data.getChild("inventory." + type + ".selected");
		int amount = 0;
		if(selectedSection != null) {
			for(ConfigSection section : selectedSection.getChildren()) {
				if(section.get("name", String.class).equals(item)) amount += section.get("amount", Integer.class, 1);
			}
		}
		return amount;
	}
	
	public void increaseAmount(String item) {
		ConfigSection selectedSection = data.getChild("inventory." + type + ".selected", true);
		Optional<ConfigSection> optional = selectedSection.getChildren().stream().filter(section -> section.get("name", String.class).equals(item)).findFirst();
		if(optional.isPresent()) {
			ConfigSection section = optional.get();
			section.set("amount", section.get("amount", Integer.class, 1) + 1);
			data.save();
		}
		else {
			for(int i = 35; i >= 0; i--) { // 36 - 40 are designated for armor / offhand
				ConfigSection slotSection = selectedSection.getChild(String.valueOf(i));
				if(slotSection == null) {
					slotSection = selectedSection.getChild(String.valueOf(i), true);
					slotSection.set("name", item);
					slotSection.set("amount", 1);
					data.save();
					return;
				}
			}
			throw new IllegalStateException("Could not find available spot for item '" + item + "'!");
		}
	}
	
	public void decreaseAmount(String item) {
		ConfigSection selectedSection = data.getChild("inventory." + type + ".selected");
		if(selectedSection == null) throw new IllegalStateException("Player does not have any items selected.");
		Optional<ConfigSection> optional = selectedSection.getChildren().stream().filter(section -> section.get("name", String.class).equals(item)).findFirst();
		if(optional.isPresent()) {
			ConfigSection section = optional.get();
			int amount = section.get("amount", Integer.class, 1);
			if(amount == 1) selectedSection.remove(section.getName());
			else section.set("amount", amount - 1);
			data.save();
		}
		else throw new IllegalArgumentException("Item '" + item + "' is not selected, couldn't decrease the amount the player has.");
	}
	
	public void populateSelectedConfiguration(ItemStack[] itemStacks) {
		ConfigSection selectedSection = data.getChild("inventory." + type + ".selected");
		if(selectedSection == null) return;
		selectedSection.getChildren().forEach(section -> {
			String slotName = section.getName();
			int slot;
			switch(slotName) {
				case "h":
					slot = 0;
					break;
				case "c":
					slot = 1;
					break;
				case "l":
					slot = 2;
					break;
				case "b":
					slot = 3;
					break;
				case "o":
					slot = 4;
					break;
				default:
					slot = Integer.parseInt(slotName);
					if(slot >= 0 && slot < 9) slot += 36;
					else slot += 9;
					break;
			}
			itemStacks[slot] = itemData.getItemInfo(section.get("name", String.class)).getItem(player, section.get("amount", Integer.class, 1));
		});
	}
	
	public void saveSelectedConfiguration(ItemStack[] itemStacks) {
		String key = "inventory." + type + ".selected";
		data.remove(key);
		ConfigSection selectedSection = data.getChild(key, true);
		for(int slot = 0; slot < 45; slot++) {
			if(slot > (Version.VERSION.isVersionAtLeast(Version.v1_9_R1) ? 4 : 3) && slot < 9) continue; // unused filled slots
			ItemStack itemStack = itemStacks[slot];
			if(!Items.exists(itemStack)) continue;
			BasicItemInfo itemInfo = itemData.getItemInfo(itemStack);
			if(itemInfo == null) throw new IllegalArgumentException("No itemInfo found for itemStack: " + itemStack);
			String slotKey;
			if(slot < 4) { // armor
				if(slot == 0) slotKey = "h";
				else if(slot == 1) slotKey = "c";
				else if(slot == 2) slotKey = "l";
				else slotKey = "b";
			}
			else if(slot == 4) slotKey = "o"; // offhand
			else if(slot >= 36) slotKey = String.valueOf(slot - 36); // hotbar
			else slotKey = String.valueOf(slot - 9); // inside inventory
			ConfigSection section = selectedSection.getChild(slotKey, true);
			section.set("name", itemInfo.getName());
			int amount = itemStack.getAmount();
			if(amount > 1) section.set("amount", amount);
		}
		data.save();
	}
	
	public void giveKit() {
		ConfigSection selectedSection = data.getChild("inventory." + type + ".selected");
		if(selectedSection == null) return;
		PlayerInventory inv = player.getPlayer().getInventory();
		selectedSection.getChildren().forEach(section -> {
			String slotName = section.getName();
			switch(slotName) {
				case "h":
					inv.setHelmet(itemData.getItemInfo(section.get("name", String.class)).getItem(player, section.get("amount", Integer.class, 1)));
					break;
				case "c":
					inv.setChestplate(itemData.getItemInfo(section.get("name", String.class)).getItem(player, section.get("amount", Integer.class, 1)));
					break;
				case "l":
					inv.setLeggings(itemData.getItemInfo(section.get("name", String.class)).getItem(player, section.get("amount", Integer.class, 1)));
					break;
				case "b":
					inv.setBoots(itemData.getItemInfo(section.get("name", String.class)).getItem(player, section.get("amount", Integer.class, 1)));
					break;
				case "o":
					// TODO
					break;
				default:
					inv.setItem(Integer.parseInt(slotName), itemData.getItemInfo(section.get("name", String.class)).getItem(player, section.get("amount", Integer.class, 1)));
					break;
			}
		});
	}
	
	public int getAmountOfItemsOfSameType(String item) {
		ConfigSection selectedSection = data.getChild("inventory." + type + ".selected");
		int amount = 0;
		if(selectedSection != null) {
			Set<String> alreadyCheckedItems = Sets.newHashSet(item);
			selectedSection.getChildren().forEach(section -> {
				String selectedItem = section.get("name", String.class);
				if(!alreadyCheckedItems.contains(selectedItem) &&
						itemData.getItemInfo(selectedItem).getTierItemType().equals(itemData.getItemInfo(item).getTierItemType())) {
					alreadyCheckedItems.add(selectedItem);
				}
			});
			amount = alreadyCheckedItems.size() - 1;
		}
		return amount;
	}
	
}