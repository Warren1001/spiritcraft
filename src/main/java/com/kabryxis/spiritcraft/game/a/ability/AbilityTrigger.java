package com.kabryxis.spiritcraft.game.a.ability;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class AbilityTrigger implements Cloneable {
	
	public TriggerType type = null;
	public SpiritPlayer triggerer = null;
	public Block block = null;
	public ItemStack hand = null;
	public Item item = null;
	public Location customLoc = null;
	public boolean cancel = true;
	
	public Location getOptimalLocation(Location def) {
		if(customLoc != null) return customLoc;
		if(item != null) return item.getLocation();
		if(triggerer != null) return triggerer.getLocation();
		return def;
	}
	
	@Override
	public AbilityTrigger clone() {
		AbilityTrigger clone;
		try {
			clone = (AbilityTrigger)super.clone();
		} catch(CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		clone.type = type;
		clone.triggerer = triggerer;
		clone.block = block;
		clone.hand = hand;
		clone.item = item;
		clone.customLoc = customLoc;
		clone.cancel = cancel;
		return clone;
	}
	
}
