package com.kabryxis.spiritcraft.game.item;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.inventory.DynamicInventory;
import com.kabryxis.spiritcraft.game.inventory.InventoryManager;
import com.kabryxis.spiritcraft.game.inventory.OpenNextInventoryAction;
import com.kabryxis.spiritcraft.game.inventory.SpiritInventory;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemData {
	
	private final Map<String, TypeData> typeDataMap = new HashMap<>();
	private final Map<String, BasicItemInfo> allItemInfos = new HashMap<>();
	
	private final ItemManager itemManager;
	private final InventoryManager inventoryManager;
	private final boolean ghost;
	private final String typeName;
	private final String itemTitle;
	private final DynamicInventory inv;
	
	public ItemData(ItemManager itemManager, boolean ghost) {
		this.itemManager = itemManager;
		this.inventoryManager = itemManager.getGame().getInventoryManager();
		this.ghost = ghost;
		this.typeName = ghost ? "ghost" : "hunter";
		this.itemTitle = (ghost ? ChatColor.DARK_GRAY : ChatColor.DARK_RED) + "Choose {type} item";
		this.inv = new SpiritInventory(inventoryManager, (ghost ? ChatColor.DARK_GRAY : ChatColor.DARK_RED) + "Choose item type to modify", 2);
		inv.setInteractablePlayerItem(9, inventoryManager.getPreviousInventoryItem());
		inv.setInteractablePlayerItem(13, inventoryManager.getInformationItem());
		inv.setInteractablePlayerItem(17, inventoryManager.getErrorItem());
	}
	
	public DynamicInventory getInventory() {
		return inv;
	}
	
	public void reload() {
		ConfigSection section = itemManager.getGlobalItemData().get(typeName);
		section.getChildren().forEach(child -> typeDataMap.put(child.getName(), new TypeData(this, typeName, child)));
		for(TypeData typeData : typeDataMap.values()) {
			String type = typeData.getItemType();
			DynamicInventory typeInv = new SpiritInventory(inventoryManager, itemTitle.replace("{type}", type), 2);
			typeDataMap.get(type).getItemInfos().forEach(itemInfo -> typeInv.setPlayerItem(itemInfo.getPreviewSlot(), (player, itemStack) -> {
				SpiritPlayer spiritPlayer = itemManager.getGame().getPlayerManager().getPlayer(player);
				return itemInfo.getItem(spiritPlayer, spiritPlayer.getPlayerItemInfo(ghost).getTotalAmount(itemInfo.getName()));
			}));
			typeInv.setInteractablePlayerItem(9, inventoryManager.getPreviousInventoryItem());
			typeInv.setInteractablePlayerItem(13, inventoryManager.getInformationItem());
			typeInv.setInteractablePlayerItem(17, inventoryManager.getErrorItem());
			typeData.getItemInfos().forEach(itemInfo -> typeInv.setInteractableItem(itemInfo.getPreviewSlot(), (player, right, shift) -> {
				SpiritPlayer spiritPlayer = itemManager.getGame().getPlayerManager().getPlayer(player);
				switch(itemInfo.previewClick(spiritPlayer, right, shift)) {
					case CANT_AFFORD:
						spiritPlayer.addErrorMessage(ChatColor.RED + "You cannot afford that!");
						break;
					case NO_ROOM:
						spiritPlayer.addErrorMessage(ChatColor.RED + "You do not have enough inventory space for that!");
						break;
					case MAX_AMOUNT:
						spiritPlayer.addErrorMessage(ChatColor.RED + "You have the maximum allowed selected of that item!");
						break;
					case MAX_TIER_AMOUNT:
						spiritPlayer.addErrorMessage(ChatColor.RED + "You are only allowed to have " + typeData.getMaxAmount() + " " + typeData.getItemType() + " " + typeData.getClassType() + " items.");
						break;
					default:
						break;
				}
				return true;
			}));
			typeInv.setPrevious(inv);
			inv.setInteractableServerItem(typeData.getIndex(), new OpenNextInventoryAction(typeInv), typeData.getItem());
		}
	}
	
	public void addItemInfo(BasicItemInfo itemInfo) {
		allItemInfos.put(itemInfo.getName(), itemInfo);
	}
	
	public BasicItemInfo getItemInfo(ItemStack representation) {
		return allItemInfos.values().stream().filter(itemInfo -> itemInfo.isOf(representation)).findFirst().orElse(null);
	}
	
	public BasicItemInfo getItemInfo(String name) {
		return allItemInfos.get(name);
	}
	
}
