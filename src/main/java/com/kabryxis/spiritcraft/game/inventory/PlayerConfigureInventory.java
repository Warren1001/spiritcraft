package com.kabryxis.spiritcraft.game.inventory;

import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.kabutils.spigot.version.Version;
import com.kabryxis.spiritcraft.game.a.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerConfigureInventory extends SpiritInventory {
	
	private static final ItemStack FILL = new ItemBuilder(Material.BARRIER).name(ChatColor.BLACK.toString()).build();
	private static final InteractableItem CANCEL_ITEM = (player, right, shift) -> true;
	private static final int FILL_START = (Version.VERSION.isVersionAtLeast(Version.v1_9_R1) ? 5 : 4);
	
	private final Game game;
	private final boolean ghost;
	
	public PlayerConfigureInventory(Game game, String name, boolean ghost) {
		super(game.getInventoryManager(), name, 6);
		InventoryManager inventoryManager = game.getInventoryManager();
		this.game = game;
		this.ghost = ghost;
		for(int i = FILL_START; i < 9; i++) {
			setInteractableServerItem(i, CANCEL_ITEM, FILL);
		}
		for(int i = 45; i < getSize(); i++) {
			setInteractableItem(i, CANCEL_ITEM);
		}
		setInteractablePlayerItem(45, inventoryManager.getPreviousInventoryItem());
		setInteractablePlayerItem(49, inventoryManager.getInformationItem());
	}
	
	@Override
	public void open(Player player) {
		ItemStack[] contents = bukkitInventory.getContents();
		game.getPlayerManager().getPlayer(player).getPlayerItemInfo(ghost).populateSelectedConfiguration(contents);
		bukkitInventory.setContents(contents);
		super.open(player);
	}
	
	@Override
	public void onClose(Player player) {
		ItemStack[] contents = bukkitInventory.getContents();
		game.getPlayerManager().getPlayer(player).getPlayerItemInfo(ghost).saveSelectedConfiguration(contents);
		for(int i = 0; i < 45; i++) {
			if(i >= FILL_START && i < 9) continue;
			contents[i] = null;
		}
		bukkitInventory.setContents(contents);
	}
	
}
