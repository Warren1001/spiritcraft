package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityCaller;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.SpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.ArrayList;
import java.util.List;

public class LookingAction extends SpiritAbilityAction {
	
	private int maxRange = 16;
	private List<AbilityCaller> abilities = new ArrayList<>();
	
	public LookingAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "looking");
		handleSubCommand("range", false, int.class, i -> maxRange = i);
		handleSubCommand("ability", true, true, data -> game.getAbilityManager().requestAbilitiesFromCommand(name, data, false, abilities::add));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		super.trigger(player, trigger);
		AbilityTrigger lookingTrigger = new AbilityTrigger();
		lookingTrigger.triggerer = player;
		lookingTrigger.type = TriggerType.LOOKING;
		lookingTrigger.block = player.getTargetBlock(maxRange);
		abilities.forEach(ability -> ability.triggerSafely(player, lookingTrigger));
	}
	
}
