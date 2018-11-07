package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.kabutils.command.Com;
import com.kabryxis.kabutils.spigot.command.BukkitCommandIssuer;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.kabutils.spigot.version.wrapper.item.itemstack.WrappedItemStack;
import com.kabryxis.spiritcraft.Spiritcraft;
import com.kabryxis.spiritcraft.game.a.tracker.ItemTrackerTest;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundCause;
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
	public void overloadsound(SpiritPlayer player) {
		player.getGame().getSoundManager().playSound("overload", new SoundCause(player.getLocation()));
	}
	
	@Com
	public void glassbreak(SpiritPlayer player) {
		Region selection = player.getSelection();
		selection.forEach(bv -> {
			Block block = player.getWorld().getBlockAt(bv.getBlockX(), bv.getBlockY(), bv.getBlockZ());
			if(block.getType() == Material.REDSTONE_LAMP_OFF || block.getType() == Material.REDSTONE_LAMP_ON) {
				plugin.getGame().getTaskManager().start(() -> block.getWorld().playSound(block.getLocation(), Sound.GLASS, 10F, 0.7F), new Random().nextInt(3));
			}
		});
	}
	
	@Com
	public void spawnitem(SpiritPlayer player, Material type, String name) {
		plugin.allowNextItemSpawn();
		Item item = player.getWorld().dropItem(player.getLocation(), new ItemBuilder(type).enchant(Enchantment.DURABILITY, 1).build());
		item.setVelocity(new Vector(0, 0, 0));
		item.setCustomName(ChatColor.translateAlternateColorCodes('&', name.replace('_', ' ')));
		item.setCustomNameVisible(true);
		player.getGame().getWorldManager().getMetadataProvider().addEmptyMetadata(item, "nopickup");
	}
	
	@Com
	public void purge(Player player) {
		player.getWorld().getEntities().stream().filter(entity -> entity.getType() != EntityType.PLAYER).forEach(Entity::remove);
	}
	
	@Com
	public void printoffsets(SpiritPlayer player, boolean print) {
		player.getDataCreator().printOffsetLocation(print);
	}
	
	@Com
	public void tracker(SpiritPlayer player) {
		if(task != null) task.cancel();
		int abilityId = Items.getTagData(player.getInventory().getItemInHand(), "AbiId", int.class);
		task = plugin.getGame().getTaskManager().start(new ItemTrackerTest(player.getItemTracker().track("AbiId" + abilityId,
				item -> Items.getTagData(item, "AbiId", int.class) == abilityId)), 0L, 5L);
	}
	
	@Com(args = "0,1")
	public void abiid(SpiritPlayer player, int length, int id) {
		WrappedItemStack wrappedItemStack = WrappedItemStack.newInstance(player.getInventory().getItemInHand());
		if(length == 0) player.sendMessage("AbiId:" + wrappedItemStack.getTag(false).get("AbiId", int.class));
		else {
			wrappedItemStack.getTag(true).set("AbiId", id);
			player.getInventory().setItemInHand(wrappedItemStack.getBukkitItemStack());
		}
	}
	
	@Com
	public void inventory(SpiritPlayer player) {
		player.getGame().getItemManager().openInventory(player);
	}
	
	@Com(args = "0,1")
	public void configure(SpiritPlayer player, int length, boolean ghost) {
		player.getGame().getItemManager().openSelectedInventory(player, length == 0 || ghost);
	}
	
	@Com
	public void saveitems(BukkitCommandIssuer issuer) {
		plugin.getGame().getItemManager().getGlobalItemData().save();
	}
	
}
