package com.kabryxis.spiritcraft.game.item;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.inventory.*;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.io.File;

public class ItemManager {
	
	private final SpiritGame game;
	private final Config globalItemData;
	private final ItemData ghostData;
	private final ItemData hunterData;
	private final DynamicInventory start;
	
	public ItemManager(SpiritGame game) {
		this.game = game;
		globalItemData = new Config(new File(game.getPlugin().getDataFolder(), "items.yml"), true);
		ghostData = new ItemData(this, true);
		hunterData = new ItemData(this, false);
		ghostData.reload();
		hunterData.reload();
		InventoryManager inventoryManager = game.getInventoryManager();
		start = new SpiritInventory(inventoryManager, ChatColor.GOLD + "Choose Class type to modify", 2);
		start.setInteractablePlayerItem(9, inventoryManager.getPreviousInventoryItem());
		start.setInteractablePlayerItem(13, inventoryManager.getInformationItem());
		start.setInteractablePlayerItem(17, inventoryManager.getErrorItem());
		DynamicInventory ghostInv = ghostData.getInventory();
		ghostInv.setPrevious(start);
		start.setInteractableServerItem(2, new OpenNextInventoryAction(ghostInv), new ItemBuilder(Material.SNOW_BALL).name(ChatColor.DARK_GRAY + "Ghost").build());
		DynamicInventory hunterInv = hunterData.getInventory();
		hunterInv.setPrevious(start);
		start.setInteractableServerItem(6, new OpenNextInventoryAction(hunterInv), new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).name(ChatColor.DARK_RED + "Hunter").build());
	}
	
	public SpiritGame getGame() {
		return game;
	}
	
	public Config getGlobalItemData() {
		return globalItemData;
	}
	
	public ItemData getItemData(boolean ghost) {
		return ghost ? ghostData : hunterData;
	}
	
	public void openInventory(SpiritPlayer player) {
		player.openInventory(start);
	}
	
	public void giveGhostKit(SpiritPlayer player) {
		player.getPlayerItemInfo(true).giveKit();
	}
	
	public void giveHunterKit(SpiritPlayer player) {
		player.getPlayerItemInfo(false).giveKit();
	}
	
	public void openSelectedInventory(SpiritPlayer player, boolean ghost) {
		player.openInventory(new PlayerConfigureInventory(game, ChatColor.GOLD + "Configure your inventory", ghost));
	}
	
}
