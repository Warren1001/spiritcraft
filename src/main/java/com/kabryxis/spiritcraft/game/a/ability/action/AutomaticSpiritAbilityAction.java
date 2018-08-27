package com.kabryxis.spiritcraft.game.a.ability.action;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;

public abstract class AutomaticSpiritAbilityAction extends AbstractSpiritAbilityAction {
	
	public AutomaticSpiritAbilityAction(String name) {
		super(name, TriggerType.values());
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		Location loc;
		if(trigger.getType() == TriggerType.THROW) loc = trigger.getItem().getLocation();
		else if(trigger.getType() == TriggerType.NEARBY) loc = trigger.getNearbyLocation();
		else if(trigger.hasBlock()) loc = trigger.getBlock().getLocation().add(0.5, 0.5, 0.5);
		else loc = player.getLocation();
		execute(loc);
	}
	
	public abstract void execute(Location loc);
	
}
