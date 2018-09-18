package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.kabutils.command.Com;
import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.spiritcraft.Spiritcraft;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Random;

@Com(name = "test")
public class TestCommand {
	
	private final Spiritcraft plugin;
	
	public TestCommand(Spiritcraft plugin) {
		this.plugin = plugin;
	}
	
	private BukkitTask task;
	
	@Com
	public void stop() {
		if(task != null) task.cancel();
	}
	
	@Com
	public void overloadsound(Player player) {
		if(task != null) task.cancel();
		task = BukkitThreads.syncTimer(new BukkitRunnable() {
			
			private final int speedInterval = 100;
			
			private int soundInterval = 30;
			private int lastTicked = 0;
			private int tick = -1;
			
			@Override
			public void run() {
				tick++;
				//System.out.println("tick:" + tick + ",lastTicked:" + lastTicked + ",-:" + (tick - lastTicked));
				if(tick != 0 && tick % speedInterval == 0) {
					soundInterval -= soundInterval <= 5 ? 1 : 5;
					//System.out.println("soundInterval: " + soundInterval);
					if(soundInterval == 2) {
						cancel();
						return;
					}
				}
				if(tick == 0 || tick - lastTicked >= soundInterval) {
					player.getWorld().playSound(player.getLocation(), Sound.PORTAL, 10F, 10F);
					lastTicked = tick;
				}
			}
			
		}, 0L, 1L);
	}
	
	@Com
	public void glassbreak(SpiritPlayer player) {
		Region selection = player.getSelection();
		selection.forEach(bv -> {
			Block block = player.getWorld().getBlockAt(bv.getBlockX(), bv.getBlockY(), bv.getBlockZ());
			if(block.getType() == Material.REDSTONE_LAMP_OFF || block.getType() == Material.REDSTONE_LAMP_ON) {
				BukkitThreads.syncLater(() -> block.getWorld().playSound(block.getLocation(), Sound.GLASS, 10F, 0.7F), new Random().nextInt(3));
			}
		});
	}
	
	@Com
	public void spawnitem(SpiritPlayer player, Material type, String name) {
		plugin.allowNextItemSpawn();
		Item item = player.getWorld().dropItem(player.getLocation(), ItemBuilder.newItemBuilder(type).enchant(Enchantment.DURABILITY, 1).build());
		item.setVelocity(new Vector(0, 0, 0));
		item.setCustomName(ChatColor.translateAlternateColorCodes('&', name.replace('_', ' ')));
		item.setCustomNameVisible(true);
		player.getGame().getWorldManager().getMetadataProvider().addEmptyMetadata(item, "nopickup");
	}
	
	@Com
	public void purge(Player player) {
		player.getWorld().getEntities().stream().filter(entity -> entity.getType() != EntityType.PLAYER).forEach(Entity::remove);
	}
	
}
