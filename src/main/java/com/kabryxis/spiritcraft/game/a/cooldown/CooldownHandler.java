package com.kabryxis.spiritcraft.game.a.cooldown;

import java.util.Arrays;

public class CooldownHandler implements Cloneable {
	
	private final CooldownManager cooldownManager;
	
	private CooldownEntry[] cooldownEntries = new CooldownEntry[Priority.values().length];
	
	public CooldownHandler(CooldownManager cooldownManager) {
		this.cooldownManager = cooldownManager;
	}
	
	public void setCooldown(CooldownEntry entry) {
		int index = entry.priority.getIndex();
		CooldownEntry curr = cooldownEntries[index];
		if(curr != null && (entry.overwriteMode == 0 || (entry.overwriteMode == 2 && curr.cooldown > entry.cooldown))) return;
		cooldownEntries[index] = entry;
	}
	
	public CooldownEntry getCooldownEntry() {
		CooldownEntry cooldown = null;
		for(int i = 0; i < cooldownEntries.length && (cooldown == null || cooldown.triggerData.getInt("abilityId") == 0); i++) {
			cooldown = cooldownEntries[i];
		}
		return cooldown;
	}
	
	public void startCooldown() {
		CooldownEntry entry = getCooldownEntry();
		if(entry != null) cooldownManager.startCooldown(entry);
	}
	
	@Override
	public CooldownHandler clone() {
		CooldownHandler clone;
		try {
			clone = (CooldownHandler)super.clone();
		} catch(CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		clone.cooldownEntries = Arrays.copyOf(cooldownEntries, cooldownEntries.length);
		return clone;
	}
	
	public enum Priority {
		
		LOW(2), NORMAL(1), HIGH(0);
		
		private final int index;
		
		Priority(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
		
	}
	
}
