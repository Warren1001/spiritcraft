package com.kabryxis.spiritcraft.game.item;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class BasicItemInfo {
	
	private final String name;
	private final boolean ghostItem;
	private final String classItemType;
	private final TypeData typeData;
	private final int maxAmount;
	private final int previewSlot;
	private final int weight;
	private final int value;
	private final ItemBuilder prebuilt;
	private final List<String> noOwnLore;
	
	public BasicItemInfo(String name, TypeData typeData, boolean ghostItem, int maxAmount, int weight, int value, int previewSlot, ItemBuilder prebuilt) {
		this.name = name;
		this.ghostItem = ghostItem;
		this.typeData = typeData;
		this.classItemType = ghostItem ? "ghost" : "hunter";
		this.maxAmount = maxAmount;
		this.previewSlot = previewSlot;
		this.weight = weight;
		this.value = value;
		this.prebuilt = prebuilt;
		this.noOwnLore = Arrays.asList(ChatColor.RESET.toString(), ChatColor.GREEN + "Click to buy for " + value + " coins!");
	}
	
	public BasicItemInfo(TypeData typeData, boolean ghostItem, ConfigSection section) {
		this.typeData = typeData;
		this.ghostItem = ghostItem;
		this.classItemType = ghostItem ? "ghost" : "hunter";
		this.name = section.getName();
		this.maxAmount = section.getInt("max_amount", 1);
		this.previewSlot = section.getInt("slot");
		this.weight = section.getInt("weight");
		this.value = section.getInt("value");
		this.prebuilt = new ItemBuilder((ConfigSection)section.get("item"));
		this.noOwnLore = Arrays.asList(ChatColor.RESET.toString(), ChatColor.GREEN + "Click to buy for " + value + " coins!");
	}
	
	public String getName() {
		return name;
	}
	
	public String getTierItemType() {
		return typeData.getItemType();
	}
	
	public int getPreviewSlot() {
		return previewSlot;
	}
	
	public SelectItemResult previewClick(SpiritPlayer player, boolean right, boolean shift) {
		PlayerItemInfo playerItemInfo = player.getPlayerItemInfo(ghostItem);
		int amount = playerItemInfo.getTotalAmount(name);
		if(right) {
			if(amount == 0) return SelectItemResult.ALREADY_UNSELECTED;
			if(shift) {
				if(amount > 1) {
					for(int i = 0; i < amount; i++) {
						playerItemInfo.decreaseAmount(name);
					}
				}
				else playerItemInfo.decreaseAmount(name);
				return SelectItemResult.UNSELECTED;
			}
			playerItemInfo.decreaseAmount(name);
			return amount == 1 ? SelectItemResult.UNSELECTED : SelectItemResult.DECREASED;
		}
		if(!playerItemInfo.owns(name)) {
			int currency = player.getCurrency();
			if(currency >= value) {
				player.setCurrency(currency - value);
				playerItemInfo.bought(name);
				return SelectItemResult.BOUGHT;
			}
			return SelectItemResult.CANT_AFFORD;
		}
		if(typeData.getMaxAmount() == playerItemInfo.getAmountOfItemsOfSameType(name)) return SelectItemResult.MAX_TIER_AMOUNT;
		if(amount == maxAmount) return SelectItemResult.MAX_AMOUNT;
		int space = player.getItemSpace();
		if(space >= weight) {
			player.setItemSpace(space - weight);
			playerItemInfo.increaseAmount(name);
			return SelectItemResult.INCREASED;
		}
		return SelectItemResult.NO_ROOM;
	}
	
	public ItemStack getItem(SpiritPlayer player, int amount) {
		PlayerItemInfo playerItemInfo = player.getPlayerItemInfo(ghostItem);
		if(!playerItemInfo.owns(name)) return prebuilt.clone().prefix(ChatColor.DARK_GRAY.toString()).lore(noOwnLore, true).build();
		ChatColor color;
		if(amount > 0) {
			prebuilt.amount(amount);
			color = ChatColor.GREEN;
		}
		else color = ChatColor.DARK_RED;
		return prebuilt.prefix(color.toString()).build();
	}
	
	public boolean isOf(ItemStack itemStack) {
		return prebuilt.isOf(itemStack, ItemBuilder.ItemCompareFlag.TYPE, ItemBuilder.ItemCompareFlag.COLORLESS_NAME);
	}
	
}
