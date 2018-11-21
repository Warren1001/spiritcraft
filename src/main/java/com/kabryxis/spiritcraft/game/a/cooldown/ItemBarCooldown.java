package com.kabryxis.spiritcraft.game.a.cooldown;

import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.ability.ItemBarTimerTask;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ItemBarCooldown implements Cooldown {
	
	private final SpiritGame game;
	private final Collection<ItemStack> items;
	
	public ItemBarCooldown(SpiritGame game, Collection<ItemStack> items) {
		this.game = game;
		this.items = items;
	}
	
	private ItemBarTimerTask cooldownTask;
	private boolean useSystemTime = false;
	private long endTime;
	
	@Override
	public void start(int tickDuration) {
		if(tickDuration > 2) {
			useSystemTime = false;
			cooldownTask = new ItemBarTimerTask(game, items, false, tickDuration, 1);
			cooldownTask.start();
		}
		else {
			cooldownTask = null;
			useSystemTime = true;
			endTime = System.currentTimeMillis() + tickDuration * 50;
		}
	}
	
	@Override
	public boolean isActive() {
		return useSystemTime ? System.currentTimeMillis() <= endTime : cooldownTask != null && cooldownTask.isRunning();
	}
	
}
