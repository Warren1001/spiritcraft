package com.kabryxis.spiritcraft.game.inv;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ItemData {
	
	private final Map<String, TypeData> typeDataMap = new HashMap<>();
	private final Map<String, BasicItemInfo> allItemInfos = new HashMap<>();
	
	private final ItemManager manager;
	private final boolean ghost;
	private final String typeName;
	private final String itemTitle;
	private final HubInventory inv;
	
	public ItemData(ItemManager manager, boolean ghost) {
		this.manager = manager;
		this.ghost = ghost;
		this.typeName = ghost ? "ghost" : "hunter";
		this.itemTitle = (ghost ? ChatColor.DARK_GRAY : ChatColor.DARK_RED) + "Choose {type} item";
		this.inv = new HubInventory(manager, (ghost ? ChatColor.DARK_GRAY : ChatColor.DARK_RED) + "Choose item type to modify", 1);
		inv.setUtility(0, manager.getPreviousInventoryAction());
		inv.setUtility(4, manager.getInformationAction());
		inv.setUtility(8, manager.getErrorAction());
	}
	
	public DynamicInventory getInventory() {
		return inv;
	}
	
	public void reload() {
		ConfigSection section = manager.getGlobalItemData().getChild(typeName);
		section.getChildren().forEach(child -> typeDataMap.put(child.getName(), new TypeData(this, typeName, child)));
		for(TypeData typeData : typeDataMap.values()) {
			String type = typeData.getItemType();
			PerPlayerInventory typeInv = new PerPlayerInventory(manager, itemTitle.replace("{type}", type), 1,
					((player, itemStacks, trueSize) -> typeDataMap.get(type).getItemInfos().forEach(itemInfo ->
							itemStacks[itemInfo.getPreviewSlot()] = itemInfo.getItem(player, player.getPlayerItemInfo(ghost).getTotalAmount(itemInfo.getName())))));
			typeInv.setUtility(0, manager.getPreviousInventoryAction());
			typeInv.setUtility(4, manager.getInformationAction());
			typeInv.setUtility(8, manager.getErrorAction());
			typeData.getItemInfos().forEach(itemInfo -> typeInv.set(itemInfo.getPreviewSlot(), (player, right, shift) -> {
				switch(itemInfo.previewClick(player, right, shift)) {
					case CANT_AFFORD:
						player.addErrorMessage(ChatColor.RED + "You cannot afford that!");
						break;
					case NO_ROOM:
						player.addErrorMessage(ChatColor.RED + "You do not have enough inventory space for that!");
						break;
					case MAX_AMOUNT:
						player.addErrorMessage(ChatColor.RED + "You have the maximum allowed selected of that item!");
						break;
					case MAX_TIER_AMOUNT:
						player.addErrorMessage(ChatColor.RED + "You are only allowed to have " + typeData.getMaxAmount() + " " + typeData.getItemType() + " " + typeData.getClassType() + " items.");
						break;
					default:
						break;
				}
			}));
			inv.set(typeData.getIndex(), typeInv, typeData.getItem());
		}
	}
	
	public void addItemInfo(BasicItemInfo itemInfo) {
		allItemInfos.put(itemInfo.getName(), itemInfo);
	}
	
	@Nullable
	public BasicItemInfo getItemInfo(ItemStack representation) {
		return allItemInfos.values().stream().filter(itemInfo -> itemInfo.isOf(representation)).findFirst().orElse(null);
	}
	
	public BasicItemInfo getItemInfo(String name) {
		return allItemInfos.get(name);
	}
	
}
