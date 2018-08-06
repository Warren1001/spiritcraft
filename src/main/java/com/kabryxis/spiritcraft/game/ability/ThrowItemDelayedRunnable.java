package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class ThrowItemDelayedRunnable extends BukkitRunnable {
	
	protected final SpiritPlayer player;
	protected final Item item;
	
	public ThrowItemDelayedRunnable(SpiritPlayer player, long delay) {
		this.player = player;
		Player p = player.getPlayer();
		item = p.getWorld().dropItem(p.getEyeLocation(), p.getItemInHand());
		p.setItemInHand(new ItemStack(Material.AIR));
		item.setVelocity(p.getLocation().getDirection().multiply(1.2));
		onThrow();
		BukkitThreads.syncLater(this, delay);
	}
	
	public abstract void onThrow();
	
	@Override
	public void run() {
		onFinish();
		item.remove();
	}
	
	public abstract void onFinish();
	
}
