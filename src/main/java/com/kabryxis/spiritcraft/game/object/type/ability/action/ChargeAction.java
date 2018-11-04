package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownHandler;
import com.kabryxis.spiritcraft.game.ability.ItemBarTimerTask;
import com.kabryxis.spiritcraft.game.object.action.GameObjectAction;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChargeAction extends SpiritGameObjectAction {
	
	private final Map<SpiritPlayer, ItemBarTimerTask> chargeTasks = new HashMap<>();
	
	private double duration = 3.5;
	private long interval = 5L;
	private boolean ltr = true;
	private List<GameObjectAction> start = new ArrayList<>();
	private List<GameObjectAction> finish = new ArrayList<>();
	
	public ChargeAction(ConfigSection creatorData) {
		super(creatorData, "charge");
		addRequiredObject("triggerer", SpiritPlayer.class);
		addRequiredObject("hand", ItemStack.class);
		handleSubCommand("duration", false, double.class, d -> duration = d);
		handleSubCommand("interval", false, long.class, l -> interval = l);
		handleSubCommand("ltr", false, boolean.class, b -> ltr = b);
		handleSubCommand("start", false, true, data -> game.getAbilityManager().createAction(data, start::add));
		handleSubCommand("finish", true, true, data -> game.getAbilityManager().createAction(data, finish::add));
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		triggerData.put("handleCooldownManually", true);
		SpiritPlayer triggerer = triggerData.get("triggerer");
		ItemBarTimerTask itemBarTimerTask = chargeTasks.get(triggerer);
		if(itemBarTimerTask == null || !itemBarTimerTask.isRunning()) {
			long uuid = Items.getTagData(triggerData.get("hand"), "uuidsc", long.class, 0L);
			itemBarTimerTask = new ItemBarTimerTask(game, triggerer.getItemTracker().track(item -> Items.getTagData(item, "uuidsc", long.class, 0L) == uuid), ltr, duration, interval) {
				
				@Override
				public void onStart() {
					super.onStart();
					start.forEach(action -> action.perform(triggerData));
				}
				
				@Override
				public void onStop() {
					super.onStop();
					triggerData.put("handleCooldownManually", false);
					finish.forEach(action -> action.perform(triggerData));
					if(!triggerData.getBoolean("handleCooldownManually", false)) triggerData.get("cooldownHandler", CooldownHandler.class).startCooldown();
				}
				
			};
			chargeTasks.put(triggerer, itemBarTimerTask);
			itemBarTimerTask.start();
		}
	}
	
	@Override
	public boolean canPerform(ConfigSection triggerData) {
		return super.canPerform(triggerData) && Items.getTagData(triggerData.get("hand"), "uuidsc", Long.class) != null;
	}
	
}
