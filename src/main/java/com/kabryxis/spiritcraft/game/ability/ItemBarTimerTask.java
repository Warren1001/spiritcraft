package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.data.Maths;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;

public class ItemBarTimerTask extends AbilityTimerRunnable {
	
	protected final Collection<ItemStack> items;
	protected final boolean ltr;
	protected final int interval;
	protected final int maxTicks;
	
	public ItemBarTimerTask(SpiritGame game, Collection<ItemStack> items, boolean ltr, int tickDuration, int interval) {
		super(game);
		this.items = items;
		this.ltr = ltr;
		this.interval = interval;
		this.maxTicks = tickDuration;
	}
	
	public ItemBarTimerTask(SpiritGame game, Collection<ItemStack> items, int tickDuration, int interval) {
		this(game, items, true, tickDuration, interval);
	}
	
	public BukkitTask start() {
		return start(0, interval, maxTicks);
	}
	
	@Override
	public void tick(int tick) {
		if(ltr) items.forEach(item -> {
			short maxDura = item.getType().getMaxDurability();
			item.setDurability((short)Math.min(maxDura - 1, maxDura - Maths.floor(maxDura * ((double)tick / (double)maxTicks))));
		});
		else items.forEach(item -> item.setDurability((short)Maths.floor(item.getType().getMaxDurability() * ((double)tick / (double)maxTicks))));
	}

	@Override
	public void onStop() {
		items.forEach(item -> item.setDurability((short)0));
	}

}
