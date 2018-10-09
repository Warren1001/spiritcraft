package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityCaller;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.SpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class InFrontAction extends SpiritAbilityAction {
	
	private double lookmod = 0.0;
	private double velmod = 0.0;
	private List<AbilityCaller> abilities = new ArrayList<>();
	
	public InFrontAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "infront");
		handleSubCommand("lookmod", false, double.class, d -> lookmod = d);
		handleSubCommand("velmod", false, double.class, d -> velmod = d);
		handleSubCommand("ability", true, true, data -> game.getAbilityManager().requestAbilitiesFromCommand(name, data, false, abilities::add));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		super.trigger(player, trigger);
		Location loc = trigger.getOptimalLocation(player.getLocation());
		AbilityTrigger clone = trigger.clone();
		clone.customLoc = loc.clone().add(loc.getDirection().clone().multiply(lookmod)).add(player.getVelocity().multiply(velmod));
		abilities.forEach(ability -> ability.trigger(player, clone));
	}
	
}
