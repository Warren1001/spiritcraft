package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.ability.ThrowItemDelayedRunnable;
import com.kabryxis.spiritcraft.game.object.TriggerType;
import com.kabryxis.spiritcraft.game.object.action.GameObjectAction;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.ArrayList;
import java.util.List;

public class ThrowItemDelayedAction extends SpiritGameObjectAction {
	
	private long delay = 60L;
	private List<GameObjectAction> thrown = new ArrayList<>();
	private List<GameObjectAction> finish = new ArrayList<>();
	
	public ThrowItemDelayedAction(ConfigSection creatorData) {
		super(creatorData, "throw_item_delayed");
		addRequiredObject("triggerer", SpiritPlayer.class);
		handleSubCommand("delay", false, long.class, l -> delay = l);
		handleSubCommand("thrown", false, true, data -> game.getAbilityManager().createAction(data, thrown::add));
		handleSubCommand("finish", false, true, data -> game.getAbilityManager().createAction(data, finish::add));
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		SpiritPlayer triggerer = triggerData.get("triggerer");
		new ThrowItemDelayedRunnable(triggerer, delay) {
			
			@Override
			public void onThrow() {
				thrown.forEach(ability -> ability.perform(triggerData));
			}
			
			@Override
			public void onFinish() {
				ConfigSection itemTriggerData = new ConfigSection();
				itemTriggerData.put("triggerer", player);
				itemTriggerData.put("type", TriggerType.THROW);
				itemTriggerData.put("item", item);
				finish.forEach(ability -> ability.perform(itemTriggerData));
			}
			
		};
	}
	
}
