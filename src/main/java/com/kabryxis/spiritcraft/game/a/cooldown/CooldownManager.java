package com.kabryxis.spiritcraft.game.a.cooldown;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;

public interface CooldownManager {
	
	void startCooldown(CooldownEntry entry);
	
	boolean isCooldownActive(AbilityTrigger trigger);
	
}
