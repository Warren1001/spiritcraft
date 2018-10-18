package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.SpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.ability.CloudTask;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public class CloudAction extends SpiritAbilityAction {
	
	public CloudAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "cloud");
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		super.trigger(player, trigger);
		new CloudTask(player.getGame(), trigger.getOptimalLocation(player.getLocation()));
	}
	
}
