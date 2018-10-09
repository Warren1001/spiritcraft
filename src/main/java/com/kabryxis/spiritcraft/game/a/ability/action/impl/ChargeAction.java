package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.spiritcraft.game.a.ability.AbilityCaller;
import com.kabryxis.spiritcraft.game.a.ability.AbilityException;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.SpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.ability.ItemBarTimerTask;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.*;

public class ChargeAction extends SpiritAbilityAction {
	
	private final Map<SpiritPlayer, ItemBarTimerTask> chargeTasks = new HashMap<>();
	
	private double duration = 3.5;
	private long interval = 5L;
	private boolean ltr = true;
	private List<AbilityCaller> start = new ArrayList<>();
	private List<AbilityCaller> finish = new ArrayList<>();
	
	public ChargeAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "charge");
		handleSubCommand("duration", false, double.class, d -> duration = d);
		handleSubCommand("interval", false, long.class, l -> interval = l);
		handleSubCommand("ltr", false, boolean.class, b -> ltr = b);
		handleSubCommand("start", false, true, data -> game.getAbilityManager().requestAbilitiesFromCommand(name, data, false, start::add));
		handleSubCommand("finish", true, true, data -> game.getAbilityManager().requestAbilitiesFromCommand(name, data, false, finish::add));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		super.trigger(player, trigger);
		trigger.handleCooldownManually = true;
		ItemBarTimerTask itemBarTimerTask = chargeTasks.get(player);
		if(itemBarTimerTask == null || !itemBarTimerTask.isRunning()) {
			long uuid = Items.getTagData(trigger.hand, "uuidsc", Long.class);
			if(uuid == 0L) throw new AbilityException("The action 'charge' can only be used on items with a uuid");
			itemBarTimerTask = new ItemBarTimerTask(player.getItemTracker().track(item -> Items.getTagData(item, "uuidsc", Long.class) == uuid), ltr, duration, interval) {
				
				@Override
				public void onStart() {
					super.onStart();
					start.forEach(ability -> ability.triggerSafely(player, trigger));
				}
				
				@Override
				public void onStop() {
					super.onStop();
					finish.forEach(ability -> ability.triggerSafely(player, trigger));
					trigger.cooldownHandler.startCooldown();
				}
				
			};
			chargeTasks.put(player, itemBarTimerTask);
			itemBarTimerTask.start();
		}
	}
	
}
