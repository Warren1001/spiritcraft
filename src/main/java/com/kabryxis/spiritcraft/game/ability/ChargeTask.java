package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.data.MathHelp;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.inventory.ItemStack;

public class ChargeTask extends AbilityTimerRunnable {
	
	protected final SpiritPlayer owner;
	protected final int itemSlot;
	protected final short maxDurability;
	protected final long interval;
	protected final int segments;
	protected final int segmentAmount;
	protected final long timeout;
	
	protected int tick = 0;
	private long stopTime = 0L;
	
	public ChargeTask(SpiritPlayer owner, double duration, long interval, long timeout) {
		this.owner = owner;
		this.itemSlot = owner.getInventory().getHeldItemSlot();
		this.maxDurability = getItem().getType().getMaxDurability();
		this.interval = interval;
		this.segments = MathHelp.ceil(duration * (20.0 / interval));
		this.segmentAmount = maxDurability / segments;
		this.timeout = timeout;
	}
	
	public ChargeTask(SpiritPlayer owner, double duration, long interval) {
		this(owner, duration, interval, 0L);
	}
	
	private ItemStack getItem() {
		return owner.getInventory().getItem(itemSlot);
	}
	
	public void start() {
		start(0L, interval);
	}
	
	@Override
	public void tick() {
		if(tick == segments) {
			getItem().setDurability((short)0);
			stop();
			return;
		}
		getItem().setDurability((short)Math.max(0, maxDurability - (segmentAmount * tick)));
		tick++;
	}
	
	@Override
	public boolean isRunning() {
		return super.isRunning() || System.currentTimeMillis() - stopTime <= 1000;
	}
	
	@Override
	public void stop() {
		super.stop();
		tick = 0;
	}
	
	@Override
	public void onStop() {
		stopTime = System.currentTimeMillis();
	}
	
}
