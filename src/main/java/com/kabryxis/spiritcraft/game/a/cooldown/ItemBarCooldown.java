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
	
	@Override
	public void start(long duration) {
		cooldownTask = new ItemBarTimerTask(game, items, false, duration / 1000.0, 1L);
		cooldownTask.start();
	}
	
	@Override
	public boolean isActive() {
		return cooldownTask != null && cooldownTask.isRunning();
	}
	
}
