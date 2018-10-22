package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.ability.ThrowItemTimerRunnable;
import com.kabryxis.spiritcraft.game.object.TriggerType;
import com.kabryxis.spiritcraft.game.object.action.GameObjectAction;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.ArrayList;
import java.util.List;

public class ThrowItemTimerAction extends SpiritGameObjectAction {
	
	private long duration = 3000L, interval = 20L;
	private List<GameObjectAction> thrown = new ArrayList<>();
	private List<GameObjectAction> tick = new ArrayList<>();
	private List<GameObjectAction> finish = new ArrayList<>();
	
	public ThrowItemTimerAction(ConfigSection creatorData) {
		super(creatorData, "throw_item_timer");
		addRequiredObject("triggerer", SpiritPlayer.class);
		handleSubCommand("duration", false, long.class, l -> duration = l);
		handleSubCommand("interval", false, long.class, l -> interval = l);
		handleSubCommand("thrown", false, true, data -> game.getAbilityManager().createAction(data, thrown::add));
		handleSubCommand("tick", false, true, data -> game.getAbilityManager().createAction(data, tick::add));
		handleSubCommand("finish", false, true, data -> game.getAbilityManager().createAction(data, finish::add));
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		SpiritPlayer triggerer = triggerData.get("triggerer");
		new ThrowItemTimerRunnable(triggerer, interval, duration) {
			
			@Override
			public void onThrow() {
				thrown.forEach(ability -> ability.perform(triggerData));
			}
			
			@Override
			public void onTick() {
				ConfigSection thrownTiggerData = new ConfigSection();
				thrownTiggerData.put("triggerer", player);
				thrownTiggerData.put("type", TriggerType.THROW);
				thrownTiggerData.put("item", item);
				tick.forEach(ability -> ability.perform(thrownTiggerData));
			}
			
			@Override
			public void onFinish() {
				ConfigSection thrownTiggerData = new ConfigSection();
				thrownTiggerData.put("triggerer", player);
				thrownTiggerData.put("type", TriggerType.THROW);
				thrownTiggerData.put("item", item);
				finish.forEach(ability -> ability.perform(thrownTiggerData));
			}
			
		};
	}
	
}
