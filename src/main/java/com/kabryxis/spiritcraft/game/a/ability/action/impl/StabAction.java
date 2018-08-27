package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.ability.StabTask;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.HashMap;
import java.util.Map;

public class StabAction extends AbstractSpiritAbilityAction {
	
	private final Map<SpiritPlayer, StabTask> stabTasks = new HashMap<>();
	
	private double duration = 3.0;
	private int updatesPerSecond = 5;
	
	public StabAction() {
		super("stab", TriggerType.values());
		registerSubCommandHandler("duration", false, true, data -> duration = Double.parseDouble(data));
		registerSubCommandHandler("ups", false, true, data -> updatesPerSecond = Integer.parseInt(data));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		StabTask stabTask = stabTasks.get(player);
		if(stabTask == null || !stabTask.isRunning()) {
			stabTask = new StabTask(player, player.getInventory().getHeldItemSlot(), duration, updatesPerSecond);
			stabTasks.put(player, stabTask);
			stabTask.start();
		}
	}
	
}
