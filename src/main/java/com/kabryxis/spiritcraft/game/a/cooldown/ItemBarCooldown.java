package com.kabryxis.spiritcraft.game.a.cooldown;

import com.kabryxis.spiritcraft.game.ability.ItemBarTimerTask;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ItemBarCooldown implements Cooldown {
	
	private final Collection<ItemStack> items;
	
	public ItemBarCooldown(Collection<ItemStack> items) {
		this.items = items;
	}
	
	private ItemBarTimerTask cooldownTask;
	
	@Override
	public void start(long duration) {
		cooldownTask = new ItemBarTimerTask(items, false, duration / 1000.0, 1L);
		cooldownTask.start();
	}
	
	@Override
	public boolean isActive() {
		return cooldownTask != null && cooldownTask.isRunning();
	}
	
}
