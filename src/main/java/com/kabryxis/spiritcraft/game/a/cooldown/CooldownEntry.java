package com.kabryxis.spiritcraft.game.a.cooldown;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;

public class CooldownEntry {
	
	public long cooldown;
	public ConfigSection triggerData;
	public CooldownHandler.Priority priority;
	public int overwriteMode;
	
	public CooldownEntry(CooldownHandler.Priority priority, long cooldown, ConfigSection triggerData, int overwriteMode) {
		this.priority = priority;
		this.cooldown = cooldown;
		this.triggerData = triggerData;
		this.overwriteMode = overwriteMode;
	}
	
	public CooldownEntry(CooldownHandler.Priority priority, long cooldown, ConfigSection triggerData) {
		this(priority, cooldown, triggerData, 2);
	}
	
	public CooldownEntry(long cooldown, ConfigSection triggerData) {
		this(CooldownHandler.Priority.NORMAL, cooldown, triggerData, 2);
	}
	
	public CooldownEntry() {
		this(CooldownHandler.Priority.NORMAL, 0L, null, 2);
	}
	
}
