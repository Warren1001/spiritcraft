package com.kabryxis.spiritcraft.game.inventory;

public class PlayerConfigureInventory {
	
	/*private static final ItemStack FILL = new ItemBuilder(Material.BARRIER).name(ChatColor.BLACK.toString()).build();
	private static final InteractableItem CANCEL_ITEM = (player, right, shift) -> true;
	private static final int FILL_START = 5;
	
	private final SpiritGame game;
	private final boolean ghost;
	
	public PlayerConfigureInventory(SpiritGame game, String name, boolean ghost) {
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
	}*/
	
}
