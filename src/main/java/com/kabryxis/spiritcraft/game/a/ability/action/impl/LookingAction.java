package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityCaller;
import com.kabryxis.spiritcraft.game.a.ability.AbilityManager;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.ArrayList;
import java.util.List;

public class LookingAction extends AbstractSpiritAbilityAction {
	
	private int maxRange = 16;
	private List<AbilityCaller> abilities = new ArrayList<>();
	
	public LookingAction(AbilityManager abilityManager) {
		super("looking", TriggerType.values());
		getParseHandler().registerSubCommandHandler("range", false, int.class, i -> maxRange = i);
		getParseHandler().registerSubCommandHandler("ability", true, true, data -> abilityManager.requestAbilityFromCommand(getName(), data, false, abilities::add));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		AbilityTrigger lookingTrigger = new AbilityTrigger();
		lookingTrigger.triggerer = player;
		lookingTrigger.type = TriggerType.LOOKING;
		lookingTrigger.block = player.getTargetBlock(maxRange);
		abilities.forEach(ability -> ability.triggerSafely(player, lookingTrigger));
	}
	
}
