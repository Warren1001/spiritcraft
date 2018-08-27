package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.action.AutomaticSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.ability.CloudTask;
import org.bukkit.Location;

public class CloudAction extends AutomaticSpiritAbilityAction {
	
	public CloudAction() {
		super("cloud");
	}
	
	@Override
	public void execute(Location loc) {
		new CloudTask(loc);
	}
	
}
