package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.ability.CloudTask;
import com.kabryxis.spiritcraft.game.object.Triggers;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;

public class CloudAction extends SpiritGameObjectAction {
	
	public CloudAction(ConfigSection creatorData) {
		super(creatorData, "cloud");
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		new CloudTask(game, Triggers.getOptimalLocation(triggerData));
	}
	
}
