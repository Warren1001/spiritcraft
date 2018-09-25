package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.ability.CloudTask;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public class CloudAction extends AbstractSpiritAbilityAction {
	
	public CloudAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "cloud");
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		new CloudTask(trigger.getOptimalLocation(player.getLocation()));
	}
	
}
