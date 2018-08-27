package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.data.MathHelp;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.inventory.ItemStack;

public class ChargeTask extends AbilityRunnable {
	
	protected final SpiritPlayer owner;
	protected final int itemSlot;
	protected final short maxDurability;
	protected final int updatesPerSecond;
	protected final int segments;
	protected final int segmentAmount;
	
	protected int tick = 0;
	
	public ChargeTask(SpiritPlayer owner, int itemSlot, double duration, int updatesPerSecond) {
		this.owner = owner;
		this.itemSlot = itemSlot;
		this.maxDurability = getItem().getType().getMaxDurability();
		this.updatesPerSecond = updatesPerSecond;
		this.segments = MathHelp.ceil(duration * updatesPerSecond);
		this.segmentAmount = maxDurability / segments;
	}
	
	private ItemStack getItem() {
		return owner.getInventory().getItem(itemSlot);
	}
	
	public void start() {
		startRepeating(0L, 20L / updatesPerSecond);
	}
	
	@Override
	public void run() {
		if(tick == segments) {
			getItem().setDurability((short)0);
			stop();
			return;
		}
		getItem().setDurability((short)Math.max(0, maxDurability - (segmentAmount * tick)));
		tick++;
	}
	
	@Override
	public void stop() {
		super.stop();
		tick = 0;
	}
	
}
