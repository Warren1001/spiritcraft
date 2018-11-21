package com.kabryxis.spiritcraft.game.a.cooldown;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;

public interface CooldownManager {
	
	void startCooldown(CooldownEntry entry);
	
	boolean isCooldownActive(ConfigSection triggerData);

	boolean isCooldownActive(int abilityId);
	
}
