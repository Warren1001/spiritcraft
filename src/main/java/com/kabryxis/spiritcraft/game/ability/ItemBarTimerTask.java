package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.data.MathHelp;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ItemBarTimerTask extends AbilityTimerRunnable {
	
	protected final Collection<ItemStack> items;
	protected final boolean ltr;
	protected final long interval;
	protected final int maxTicks;
	
	protected int tick = 0;
	
	public ItemBarTimerTask(Collection<ItemStack> items, boolean ltr, double duration, long interval) {
		this.items = items;
		this.ltr = ltr;
		this.interval = interval;
		this.maxTicks = MathHelp.floor(20.0 / interval * duration);
	}
	
	public ItemBarTimerTask(Collection<ItemStack> items, double duration, long interval) {
		this(items, true, duration, interval);
	}
	
	public void start() {
		start(0L, interval);
	}
	
	@Override
	public void tick() {
		if(tick == maxTicks) {
			items.forEach(item -> item.setDurability((short)0));
			stop();
			return;
		}
		if(ltr) items.forEach(item -> {
			short maxDura = item.getType().getMaxDurability();
			item.setDurability((short)Math.min(maxDura - 1, maxDura - MathHelp.floor(maxDura * ((double)tick / (double)maxTicks))));
		});
		else items.forEach(item -> item.setDurability((short)MathHelp.floor(item.getType().getMaxDurability() * ((double)tick / (double)maxTicks))));
		tick++;
	}
	
}
