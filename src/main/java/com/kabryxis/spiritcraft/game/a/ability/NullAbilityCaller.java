package com.kabryxis.spiritcraft.game.a.ability;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public class NullAbilityCaller implements AbilityCaller {
	
	private final String owningCommand;
	private final String abilityCommand;
	
	public NullAbilityCaller(String owningCommand, String abilityCommand) {
		this.owningCommand = owningCommand;
		this.abilityCommand = abilityCommand;
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		throw new UnsupportedOperationException("The command '" + owningCommand + "' tried to trigger the ability '" + abilityCommand + "' but the ability does not exist.");
	}
	
	@Override
	public boolean hasTriggerType(TriggerType triggerType) {
		throw new UnsupportedOperationException("The command '" + owningCommand + "' tried to retrieve data from the ability '" + abilityCommand + "' but the ability does not exist.");
	}
	
	@Override
	public String getName() {
		throw new UnsupportedOperationException("The command '" + owningCommand + "' tried to retrieve data from the ability '" + abilityCommand + "' but the ability does not exist.");
	}
	
}
