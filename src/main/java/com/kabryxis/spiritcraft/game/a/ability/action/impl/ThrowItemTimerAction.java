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
	
	private long duration = 3000L, interval = 20L;
	private List<AbilityCaller> thrown = new ArrayList<>();
	private List<AbilityCaller> tick = new ArrayList<>();
	private List<AbilityCaller> finish = new ArrayList<>();
	
	public ThrowItemTimerAction(AbilityManager abilityManager) {
		super("throw_item_timer");
		getParseHandler().registerSubCommandHandler("duration", false, long.class, l -> duration = l);
		getParseHandler().registerSubCommandHandler("interval", false, long.class, l -> interval = l);
		getParseHandler().registerSubCommandHandler("thrown", false, true, data -> abilityManager.requestAbilitiesFromCommand(getName(), data, false, thrown::add));
		getParseHandler().registerSubCommandHandler("tick", false, true, data -> abilityManager.requestAbilitiesFromCommand(getName(), data, true, tick::add));
		getParseHandler().registerSubCommandHandler("finish", false, true, data -> abilityManager.requestAbilitiesFromCommand(getName(), data, true, finish::add));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		new ThrowItemTimerRunnable(player, interval, duration) {
			
			@Override
			public void onThrow() {
				thrown.forEach(ability -> ability.triggerSafely(player, trigger));
			}
			
			@Override
			public void onTick() {
				AbilityTrigger thrownTigger = new AbilityTrigger();
				thrownTigger.triggerer = player;
				thrownTigger.type = TriggerType.THROW;
				thrownTigger.item = item;
				tick.forEach(ability -> ability.triggerSafely(player, thrownTigger));
			}
			
			@Override
			public void onFinish() {
				AbilityTrigger thrownTigger = new AbilityTrigger();
				thrownTigger.triggerer = player;
				thrownTigger.type = TriggerType.THROW;
				thrownTigger.item = item;
				finish.forEach(ability -> ability.triggerSafely(player, thrownTigger));
			}
			
		};
	}
	
}
