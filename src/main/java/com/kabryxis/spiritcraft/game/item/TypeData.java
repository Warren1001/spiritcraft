package com.kabryxis.spiritcraft.game.item;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TypeData {
	
	private final Map<String, BasicItemInfo> itemInfoMap = new HashMap<>();
	
	private final String classType;
	private final String itemType;
	private final int index;
	private final int maxAmount;
	private final ItemBuilder prebuilt;
	
	public TypeData(String classType, String itemType, int index, int maxAmount, ItemBuilder prebuilt, BasicItemInfo... itemInfos) {
		this.classType = classType;
		this.itemType = itemType;
		this.index = index;
		this.maxAmount = maxAmount;
		this.prebuilt = prebuilt;
		for(BasicItemInfo itemInfo : itemInfos) {
			itemInfoMap.put(itemInfo.getName(), itemInfo);
		}
	}
	
	public TypeData(ItemData data, String type, ConfigSection section) {
		this.classType = type;
		this.itemType = section.getName();
		this.index = section.get("slot", Integer.class);
		this.maxAmount = section.get("max-amount", Integer.class, 64);
		this.prebuilt = ItemBuilder.newItemBuilder(section.getChild("item"));
		ConfigSection itemsSection = section.getChild("items");
		itemsSection.getChildren().forEach(child -> {
			BasicItemInfo itemInfo = new BasicItemInfo(this, type.equals("ghost"), child);
			itemInfoMap.put(child.getName(), itemInfo);
			data.addItemInfo(itemInfo);
		});
	}
	
	public String getClassType() {
		return classType;
	}
	
	public String getItemType() {
		return itemType;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getMaxAmount() {
		return maxAmount;
	}
	
	public BasicItemInfo getItemInfo(String item) {
		return itemInfoMap.get(item);
	}
	
	public Collection<BasicItemInfo> getItemInfos() {
		return itemInfoMap.values();
	}
	
	public ItemStack getItem() {
		return prebuilt.build();
	}
	
}
