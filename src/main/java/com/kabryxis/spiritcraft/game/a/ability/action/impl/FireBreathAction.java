package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.ability.FireBreathTask;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public class FireBreathAction extends AbstractSpiritAbilityAction {
	
	public FireBreathAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "fire_breath");
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		new FireBreathTask(player);
	}
	
}
