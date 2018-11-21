package com.kabryxis.spiritcraft.game.object.action;

import com.kabryxis.kabutils.data.Arrays;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownEntry;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownHandler;
import com.kabryxis.spiritcraft.game.object.SpiritGameObject;
import com.kabryxis.spiritcraft.game.object.TriggerType;

import java.util.HashMap;
import java.util.Map;

public abstract class SpiritGameObjectAction extends SpiritGameObject implements GameObjectAction {
	
	protected final Map<String, Class<?>> requiredObjects = new HashMap<>();
	
	protected final TriggerType[] triggerTypes;
	
	protected int cooldown = -1;
	
	public SpiritGameObjectAction(ConfigSection creatorData, String name, TriggerType... triggerTypes) {
		super(creatorData, name);
		this.triggerTypes = triggerTypes == null || triggerTypes.length == 0 ? TriggerType.values() : triggerTypes;
		handleSubCommand("cooldown", false, int.class, l -> cooldown = l);
	}
	
	public void addRequiredObject(String key, Class<?> typeClass) {
		requiredObjects.put(key, typeClass);
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		if(cooldown > 0) {
			CooldownHandler cooldownHandler = triggerData.get("cooldownHandler");
			if(cooldownHandler != null) cooldownHandler.setCooldown(new CooldownEntry(cooldown, triggerData));
		}
	}
	
	@Override
	public boolean hasTriggerType(TriggerType type) {
		return Arrays.contains(triggerTypes, type);
	}
	
	@Override
	public boolean canPerform(ConfigSection triggerData) {
		return hasTriggerType(triggerData.get("type")) && requiredObjects.entrySet().stream().allMatch(entry -> triggerData.get(entry.getKey(), entry.getValue()) != null);
	}
	
}
