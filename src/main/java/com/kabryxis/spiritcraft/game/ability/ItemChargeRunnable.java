package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.data.MathHelp;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ItemChargeRunnable extends BukkitRunnable {
	
	protected final SpiritPlayer owner;
	protected final ItemStack item;
	protected final short maxDurability;
	protected final int updatesPerSecond;
	protected final int segments;
	protected final int segmentAmount;
	
	public ItemChargeRunnable(SpiritPlayer owner, ItemStack item, double duration, int updatesPerSecond) {
		this.owner = owner;
		this.item = item;
		this.maxDurability = item.getType().getMaxDurability();
		this.updatesPerSecond = updatesPerSecond;
		this.segments = MathHelp.ceil(duration * updatesPerSecond);
		this.segmentAmount = maxDurability / segments;
	}
	
	protected int tick = 0;
	
	@Override
	public void run() {
		if(tick == segments) {
			item.setDurability((short)0);
			onFinish();
			reset();
			cancel();
			return;
		}
		item.setDurability((short)Math.max(0, maxDurability - (segmentAmount * tick)));
		tick++;
	}
	
	public BukkitTask start() {
		onStart();
		return runTaskTimer(owner.getGame().getPlugin(), 0L, 20L / updatesPerSecond);
	}
	
	public void reset() {
		tick = 0;
	}
	
	public void onStart() {}
	
	public void onFinish() {}
	
}
