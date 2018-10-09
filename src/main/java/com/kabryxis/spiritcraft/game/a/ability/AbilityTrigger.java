package com.kabryxis.spiritcraft.game.a.ability;

import com.kabryxis.spiritcraft.game.a.cooldown.CooldownHandler;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class AbilityTrigger implements Cloneable {
	
	public CooldownHandler cooldownHandler;
	public TriggerType type = null;
	public SpiritPlayer triggerer = null;
	public Block block = null;
	public ItemStack hand = null;
	public Item item = null;
	public Location customLoc = null;
	public int abilityId = 0;
	public boolean handleCooldownManually = false;
	public boolean cancel = true;
	
	public Location getOptimalLocation(Location def) {
		if(customLoc != null) return customLoc;
		if(item != null) return item.getLocation();
		if(type == TriggerType.LOOKING && block != null) return block.getLocation();
		if(triggerer != null) return triggerer.getLocation();
		return def;
	}
	
	public boolean isDirectInteraction() {
		return type == TriggerType.LEFT_CLICK || type == TriggerType.RIGHT_CLICK;
	}
	
	@Override
	public AbilityTrigger clone() {
		AbilityTrigger clone;
		try {
			clone = (AbilityTrigger)super.clone();
		} catch(CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		clone.cooldownHandler = cooldownHandler.clone();
		/*clone.type = type;
		clone.triggerer = triggerer;
		clone.block = block;
		clone.hand = hand;
		clone.item = item;
		clone.customLoc = customLoc;
		clone.abilityId = abilityId;
		clone.handleCooldownManually = handleCooldownManually;
		clone.cancel = cancel;*/
		return clone;
	}
	
}
