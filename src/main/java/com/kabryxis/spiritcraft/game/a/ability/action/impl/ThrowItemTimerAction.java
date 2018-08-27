package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityCaller;
import com.kabryxis.spiritcraft.game.a.ability.AbilityManager;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.ability.ThrowItemTimerRunnable;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.ArrayList;
import java.util.List;

public class ThrowItemTimerAction extends AbstractSpiritAbilityAction {
	
	private long duration = 3000, interval = 20;
	private List<AbilityCaller> thrown = new ArrayList<>();
	private List<AbilityCaller> tick = new ArrayList<>();
	private List<AbilityCaller> finish = new ArrayList<>();
	
	public ThrowItemTimerAction(AbilityManager abilityManager) {
		super("throw_item_timer", TriggerType.values());
		registerSubCommandHandler("duration", false, true, data -> duration = Integer.parseInt(data));
		registerSubCommandHandler("interval", false, true, data -> interval = Integer.parseInt(data));
		registerSubCommandHandler("thrown", false, true, data -> abilityManager.requestAbilitiesFromCommand(getName(), data, false, thrown::add));
		registerSubCommandHandler("tick", false, true, data -> abilityManager.requestAbilitiesFromCommand(getName(), data, true, tick::add));
		registerSubCommandHandler("finish", false, true, data -> abilityManager.requestAbilitiesFromCommand(getName(), data, true, finish::add));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		new ThrowItemTimerRunnable(player, interval, duration) {
			
			private final AbilityTrigger thrownTigger = new AbilityTrigger(item);
			
			@Override
			public void onThrow() {
				thrown.forEach(ability -> ability.triggerSafely(player, trigger));
			}
			
			@Override
			public void onTick() {
				tick.forEach(ability -> ability.triggerSafely(player, thrownTigger));
			}
			
			@Override
			public void onFinish() {
				finish.forEach(ability -> ability.triggerSafely(player, thrownTigger));
			}
			
		};
	}
	
}
