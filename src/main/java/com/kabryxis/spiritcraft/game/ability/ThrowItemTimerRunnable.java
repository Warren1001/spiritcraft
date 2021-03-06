package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class ThrowItemTimerRunnable extends BukkitRunnable {
	
	protected final SpiritPlayer player;
	protected final Item item;
	private final long finishTime;
	
	public ThrowItemTimerRunnable(SpiritPlayer player, long interval, long duration) {
		this.player = player;
		Player p = player.getPlayer();
		ItemStack itemStack = p.getInventory().getItemInMainHand().clone();
		itemStack.setAmount(1);
		item = Items.dropItem(p.getEyeLocation(), itemStack);
		item.setVelocity(p.getLocation().getDirection().multiply(1.2));
		onThrow();
		finishTime = System.currentTimeMillis() + duration;
		player.getGame().getTaskManager().start(this, interval, interval);
	}
	
	public abstract void onThrow();
	
	@Override
	public void run() {
		if(System.currentTimeMillis() >= finishTime) {
			onFinish();
			item.remove();
			cancel();
			return;
		}
		onTick();
	}
	
	public abstract void onTick();
	
	public abstract void onFinish();
	
}
