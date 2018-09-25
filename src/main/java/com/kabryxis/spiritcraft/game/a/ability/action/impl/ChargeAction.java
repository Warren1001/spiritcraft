package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityCaller;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.ability.ChargeTask;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChargeAction extends AbstractSpiritAbilityAction {
	
	private final Map<SpiritPlayer, ChargeTask> chargeTasks = new HashMap<>();
	
	private double duration = 3.5;
	private long interval = 5L;
	private long timeout = 0L;
	private List<AbilityCaller> start = new ArrayList<>();
	private List<AbilityCaller> finish = new ArrayList<>();
	
	public ChargeAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "charge");
		handleSubCommand("duration", false, double.class, d -> duration = d);
		handleSubCommand("interval", false, long.class, l -> interval = l);
		handleSubCommand("timeout", false, long.class, l -> timeout = l);
		handleSubCommand("start", false, true, data -> game.getAbilityManager().requestAbilityFromCommand(name, data, false, start::add));
		handleSubCommand("finish", true, true, data -> game.getAbilityManager().requestAbilityFromCommand(name, data, false, finish::add));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		ChargeTask chargeTask = chargeTasks.get(player);
		if(chargeTask == null || !chargeTask.isRunning()) {
			chargeTask = new ChargeTask(player, duration, interval, timeout) {
				
				@Override
				public void onStart() {
					super.onStart();
					start.forEach(ability -> ability.triggerSafely(player, trigger));
				}
				
				@Override
				public void onStop() {
					super.onStop();
					finish.forEach(ability -> ability.triggerSafely(player, trigger));
				}
				
			};
			chargeTasks.put(player, chargeTask);
			chargeTask.start();
		}
	}
	
}
