package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityCaller;
import com.kabryxis.spiritcraft.game.a.ability.AbilityManager;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.List;

public class LookingAction extends AbstractSpiritAbilityAction {
	
	private int maxRange = 16;
	private List<AbilityCaller> abilities;
	
	public LookingAction(AbilityManager abilityManager) {
		super("looking", TriggerType.values());
		registerSubCommandHandler("range", false, true, data -> maxRange = Integer.parseInt(data));
		registerSubCommandHandler("ability", true, false, data -> abilityManager.requestAbilityFromCommand(getName(), data, false, abilities::add));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		AbilityTrigger lookingTrigger = new AbilityTrigger(TriggerType.LOOKING, player.getTargetBlock(maxRange));
		abilities.forEach(ability -> ability.triggerSafely(player, lookingTrigger));
	}
	
}
