package com.kabryxis.spiritcraft.game.a.cooldown;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;

import java.util.function.Supplier;

public class CooldownEntry {
	
	public int cooldown;
	public ConfigSection triggerData;
	public CooldownHandler.Priority priority;
	public int overwriteMode;
	public Supplier<? extends Cooldown> supplier;
	
	public CooldownEntry(CooldownHandler.Priority priority, int cooldown, ConfigSection triggerData, int overwriteMode) {
		this.priority = priority;
		this.cooldown = cooldown;
		this.triggerData = triggerData;
		this.overwriteMode = overwriteMode;
	}
	
	public CooldownEntry(CooldownHandler.Priority priority, int cooldown, ConfigSection triggerData) {
		this(priority, cooldown, triggerData, 2);
	}
	
	public CooldownEntry(int cooldown, ConfigSection triggerData) {
		this(CooldownHandler.Priority.NORMAL, cooldown, triggerData, 2);
	}
	
	public CooldownEntry() {
		this(CooldownHandler.Priority.NORMAL, 0, null, 2);
	}
	
}
