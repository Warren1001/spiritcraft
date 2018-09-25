package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityCaller;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.ability.ThrowItemTimerRunnable;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.ArrayList;
import java.util.List;

public class ThrowItemTimerAction extends AbstractSpiritAbilityAction {
	
	private long duration = 3000L, interval = 20L;
	private List<AbilityCaller> thrown = new ArrayList<>();
	private List<AbilityCaller> tick = new ArrayList<>();
	private List<AbilityCaller> finish = new ArrayList<>();
	
	public ThrowItemTimerAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "throw_item_timer");
		handleSubCommand("duration", false, long.class, l -> duration = l);
		handleSubCommand("interval", false, long.class, l -> interval = l);
		handleSubCommand("thrown", false, true, data -> game.getAbilityManager().requestAbilityFromCommand(name, data, false, thrown::add));
		handleSubCommand("tick", false, true, data -> game.getAbilityManager().requestAbilityFromCommand(name, data, true, tick::add));
		handleSubCommand("finish", false, true, data -> game.getAbilityManager().requestAbilityFromCommand(name, data, true, finish::add));
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
