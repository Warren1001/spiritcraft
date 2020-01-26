package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.data.Maths;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
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
			int maxDura = item.getType().getMaxDurability();
			ItemMeta meta = item.getItemMeta();
			((Damageable)meta).setDamage(Math.min(maxDura - 1, maxDura - Maths.floor(maxDura * ((double)tick / (double)maxTicks))));
			item.setItemMeta(meta);
		});
		else items.forEach(item -> {
			ItemMeta meta = item.getItemMeta();
			((Damageable)meta).setDamage(Maths.floor(item.getType().getMaxDurability() * ((double)tick / (double)maxTicks)));
			item.setItemMeta(meta);
		});
	}

	@Override
	public void onStop() {
		items.forEach(item -> {
			ItemMeta meta = item.getItemMeta();
			((Damageable)meta).setDamage(0);
			item.setItemMeta(meta);
		});
	}

}
