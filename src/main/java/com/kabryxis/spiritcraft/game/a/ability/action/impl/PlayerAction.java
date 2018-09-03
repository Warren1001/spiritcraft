package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public class PlayerAction extends AbstractSpiritAbilityAction {
	
	private double damage = 0.0;
	
	public PlayerAction() {
		super("player");
		getParseHandler().registerSubCommandHandler("damage", false, double.class, d -> damage = d);
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		execute(player);
	}
	
	private void execute(SpiritPlayer player) {
		if(damage > 0.0) player.damage(Math.min(damage, player.getMaxHealth()));
	}
	
}
