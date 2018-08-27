package com.kabryxis.spiritcraft.game.a.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class AbilityTrigger {
	
	private final TriggerType type;
	private Block block = null;
	private ItemStack hand = null;
	private Item item = null;
	private Location nearbyLoc = null;
	
	private boolean cancel = true;
	
	public AbilityTrigger(TriggerType type) {
		this.type = type;
	}
	
	public AbilityTrigger(TriggerType type, ItemStack hand) {
		this.type = type;
		this.hand = hand == null || hand.getType() == Material.AIR ? null : hand;
	}
	
	public AbilityTrigger(TriggerType type, Block block) {
		this.type = type;
		this.block = block;
	}
	
	public AbilityTrigger(TriggerType type, ItemStack hand, Block block) {
		this.type = type;
		this.block = block;
		this.hand = hand == null || hand.getType() == Material.AIR ? null : hand;
	}
	
	public AbilityTrigger(Item item) {
		this.type = TriggerType.THROW;
		this.item = item;
	}
	
	public AbilityTrigger(Location nearbyLoc) {
		this.type = TriggerType.NEARBY;
		this.nearbyLoc = nearbyLoc;
	}
	
	public TriggerType getType() {
		return type;
	}
	
	public boolean hasBlock() {
		return block != null;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public boolean hasHand() {
		return hand != null;
	}
	
	public ItemStack getHand() {
		return hand;
	}
	
	public Item getItem() {
		return item;
	}
	
	public Location getNearbyLocation() {
		return nearbyLoc;
	}
	
	public void cancelEvent(boolean cancel) {
		this.cancel = cancel;
	}
	
	public boolean cancelsEvent() {
		return cancel;
	}
	
}
