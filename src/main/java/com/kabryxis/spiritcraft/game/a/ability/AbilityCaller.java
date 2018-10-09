package com.kabryxis.spiritcraft.game.a.ability;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.apache.commons.lang3.Validate;

public interface AbilityCaller {
	
	default void triggerSafely(SpiritPlayer player, AbilityTrigger trigger) {
		validateTrigger(trigger);
		trigger(player, trigger);
	}
	
	default void validateTrigger(AbilityTrigger trigger) {
		TriggerType triggerType = trigger.type;
		Validate.isTrue(hasTriggerType(triggerType), "The ability '%s' cannot be triggered by %s.", getName(), triggerType.name());
	}
	
	void trigger(SpiritPlayer player, AbilityTrigger trigger);
	
	boolean hasTriggerType(TriggerType triggerType);
	
	String getName();
	
}
