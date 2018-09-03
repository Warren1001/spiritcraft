package com.kabryxis.spiritcraft.game.a.ability.action;

import com.kabryxis.kabutils.data.Arrays;
import com.kabryxis.spiritcraft.game.a.ability.AbilityParseHandler;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;

public abstract class AbstractSpiritAbilityAction extends AbilityParseHandler implements AbilityAction {
	
	private final String name;
	private final TriggerType[] supportedTriggerTypes;
	
	public AbstractSpiritAbilityAction(String name, TriggerType... supportedTriggerTypes) {
		super(name);
		this.name = name;
		this.supportedTriggerTypes = supportedTriggerTypes.length == 0 ? TriggerType.values() : supportedTriggerTypes;
	}
	
	@Override
	public boolean hasTriggerType(TriggerType triggerType) {
		return Arrays.contains(supportedTriggerTypes, triggerType);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
}
