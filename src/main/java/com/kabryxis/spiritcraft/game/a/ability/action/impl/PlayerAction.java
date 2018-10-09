package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.SpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public class PlayerAction extends SpiritAbilityAction {
	
	private double damage = 0.0;
	
	public PlayerAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "player");
		handleSubCommand("damage", false, double.class, d -> damage = d);
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		super.trigger(player, trigger);
		execute(player);
	}
	
	private void execute(SpiritPlayer player) {
		if(damage > 0.0) player.damage(damage);
	}
	
}
