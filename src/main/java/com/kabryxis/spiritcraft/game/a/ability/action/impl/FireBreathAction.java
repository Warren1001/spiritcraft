package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.ability.FireBreathTask;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public class FireBreathAction extends AbstractSpiritAbilityAction {
	
	public FireBreathAction() {
		super("fire_breath", TriggerType.values());
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		new FireBreathTask(player);
	}
	
}
