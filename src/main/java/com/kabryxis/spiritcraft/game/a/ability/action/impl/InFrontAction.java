package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityCaller;
import com.kabryxis.spiritcraft.game.a.ability.AbilityManager;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class InFrontAction extends AbstractSpiritAbilityAction {
	
	private double lookmod = 0.0;
	private double velmod = 0.0;
	private List<AbilityCaller> abilities = new ArrayList<>();
	
	public InFrontAction(AbilityManager abilityManager) {
		super("infront");
		getParseHandler().registerSubCommandHandler("lookmod", false, double.class, d -> lookmod = d);
		getParseHandler().registerSubCommandHandler("velmod", false, double.class, d -> velmod = d);
		getParseHandler().registerSubCommandHandler("ability", true, true, data -> abilityManager.requestAbilitiesFromCommand(getName(), data, false, abilities::add));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		Location loc = trigger.getOptimalLocation(player.getLocation());
		AbilityTrigger clone = trigger.clone();
		clone.customLoc = loc.clone().add(loc.getDirection().clone().multiply(lookmod).add(player.getVelocity().multiply(velmod)));
		abilities.forEach(ability -> ability.trigger(player, clone));
	}
	
}
