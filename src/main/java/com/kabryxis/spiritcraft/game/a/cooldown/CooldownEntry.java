package com.kabryxis.spiritcraft.game.a.cooldown;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;

public class CooldownEntry {
	
	public long cooldown;
	public AbilityTrigger trigger;
	public CooldownHandler.Priority priority;
	public int overwriteMode;
	
	public CooldownEntry(CooldownHandler.Priority priority, long cooldown, AbilityTrigger trigger, int overwriteMode) {
		this.priority = priority;
		this.cooldown = cooldown;
		this.trigger = trigger;
		this.overwriteMode = overwriteMode;
	}
	
	public CooldownEntry(CooldownHandler.Priority priority, long cooldown, AbilityTrigger trigger) {
		this(priority, cooldown, trigger, 2);
	}
	
	public CooldownEntry(long cooldown, AbilityTrigger trigger) {
		this(CooldownHandler.Priority.NORMAL, cooldown, trigger, 2);
	}
	
	public CooldownEntry() {
		this(CooldownHandler.Priority.NORMAL, 0L, null, 2);
	}
	
}
