package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.object.TriggerType;
import com.kabryxis.spiritcraft.game.object.action.GameObjectAction;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.ArrayList;
import java.util.List;

public class LookingAction extends SpiritGameObjectAction {
	
	private int maxRange = 16;
	private List<GameObjectAction> abilities = new ArrayList<>();
	
	public LookingAction(ConfigSection creatorData) {
		super(creatorData, "looking");
		addRequiredObject("triggerer", SpiritPlayer.class);
		handleSubCommand("range", false, int.class, i -> maxRange = i);
		handleSubCommand("ability", true, true, data -> game.getAbilityManager().createAction(data, abilities::add));
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		ConfigSection lookingTriggerData = new ConfigSection();
		SpiritPlayer triggerer = triggerData.get("triggerer");
		lookingTriggerData.put("triggerer", triggerer);
		lookingTriggerData.put("type", TriggerType.LOOKING);
		lookingTriggerData.put("block", triggerer.getTargetBlock(maxRange));
		abilities.forEach(ability -> ability.perform(lookingTriggerData));
	}
	
}
