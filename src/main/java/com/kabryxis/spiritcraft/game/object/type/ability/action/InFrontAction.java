package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.object.Triggers;
import com.kabryxis.spiritcraft.game.object.action.GameObjectAction;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class InFrontAction extends SpiritGameObjectAction {
	
	private double lookmod = 0.0;
	private double velmod = 0.0;
	private List<GameObjectAction> abilities = new ArrayList<>();
	
	public InFrontAction(ConfigSection creatorData) {
		super(creatorData, "infront");
		addRequiredObject("triggerer", SpiritPlayer.class);
		handleSubCommand("lookmod", false, double.class, d -> lookmod = d);
		handleSubCommand("velmod", false, double.class, d -> velmod = d);
		handleSubCommand("ability", true, true, data -> game.getAbilityManager().createAction(data, abilities::add));
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		SpiritPlayer triggerer = triggerData.get("triggerer");
		Location loc = Triggers.getOptimalLocation(triggerData);
		ConfigSection clone = new ConfigSection(triggerData);
		clone.put("customLoc", loc.clone().add(loc.getDirection().clone().multiply(lookmod)).add(triggerer.getVelocity().multiply(velmod)));
		abilities.forEach(ability -> ability.perform(clone));
	}
	
}
