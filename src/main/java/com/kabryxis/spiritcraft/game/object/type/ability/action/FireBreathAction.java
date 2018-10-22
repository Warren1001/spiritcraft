package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.ability.FireBreathTask;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public class FireBreathAction extends SpiritGameObjectAction {
	
	public FireBreathAction(ConfigSection creatorData) {
		super(creatorData, "fire_breath");
		addRequiredObject("triggerer", SpiritPlayer.class);
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		new FireBreathTask(triggerData.get("triggerer"));
	}
	
}
