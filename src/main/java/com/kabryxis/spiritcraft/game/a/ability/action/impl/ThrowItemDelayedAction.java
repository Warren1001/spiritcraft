package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityCaller;
import com.kabryxis.spiritcraft.game.a.ability.AbilityManager;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.ability.ThrowItemDelayedRunnable;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.ArrayList;
import java.util.List;

public class ThrowItemDelayedAction extends AbstractSpiritAbilityAction {
	
	private long delay = 60L;
	private List<AbilityCaller> thrown = new ArrayList<>();
	private List<AbilityCaller> finish = new ArrayList<>();
	
	public ThrowItemDelayedAction(AbilityManager abilityManager) {
		super("throw_item_delayed", TriggerType.values());
		registerSubCommandHandler("delay", false, true, data -> delay = Integer.parseInt(data));
		registerSubCommandHandler("thrown", false, true, data -> abilityManager.requestAbilitiesFromCommand(getName(), data, false, thrown::add));
		registerSubCommandHandler("finish", false, true, data -> abilityManager.requestAbilitiesFromCommand(getName(), data, true, finish::add));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		new ThrowItemDelayedRunnable(player, delay) {
			
			@Override
			public void onThrow() {
				thrown.forEach(ability -> ability.triggerSafely(player, trigger));
			}
			
			@Override
			public void onFinish() {
				finish.forEach(ability -> ability.triggerSafely(player, new AbilityTrigger(item)));
			}
			
		};
	}
	
}
