package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityCaller;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.SpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.ability.ThrowItemDelayedRunnable;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.ArrayList;
import java.util.List;

public class ThrowItemDelayedAction extends SpiritAbilityAction {
	
	private long delay = 60L;
	private List<AbilityCaller> thrown = new ArrayList<>();
	private List<AbilityCaller> finish = new ArrayList<>();
	
	public ThrowItemDelayedAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "throw_item_delayed");
		handleSubCommand("delay", false, long.class, l -> delay = l);
		handleSubCommand("thrown", false, true, data -> game.getAbilityManager().requestAbilitiesFromCommand(name, data, false, thrown::add));
		handleSubCommand("finish", false, true, data -> game.getAbilityManager().requestAbilitiesFromCommand(name, data, true, finish::add));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		super.trigger(player, trigger);
		new ThrowItemDelayedRunnable(player, delay) {
			
			@Override
			public void onThrow() {
				thrown.forEach(ability -> ability.triggerSafely(player, trigger));
			}
			
			@Override
			public void onFinish() {
				AbilityTrigger itemTrigger = new AbilityTrigger();
				itemTrigger.type = TriggerType.THROW;
				itemTrigger.triggerer = player;
				itemTrigger.item = item;
				finish.forEach(ability -> ability.triggerSafely(player, itemTrigger));
			}
			
		};
	}
	
}
