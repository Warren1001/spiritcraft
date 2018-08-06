package com.kabryxis.spiritcraft.game;

import com.kabryxis.kabutils.command.Com;
import com.kabryxis.kabutils.spigot.command.BukkitCommandIssuer;
import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.kabutils.spigot.version.custom.slime.hitbox.CustomHitbox;
import com.kabryxis.kabutils.spigot.world.schematic.BlockSelection;
import com.kabryxis.spiritcraft.Spiritcraft;
import com.kabryxis.spiritcraft.game.ability.CloudTask;
import com.kabryxis.spiritcraft.game.ability.FireBreathTask;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;
import org.inventivetalent.particle.ParticleEffect;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommandListener implements Listener {
	
	public static void register() {
		String name = "Slime";
		Class<?> clazz = CustomHitbox.class;
		try {
			Class<?> entityTypesClass = Class.forName("net.minecraft.server.v1_8_R3.EntityTypes");
			((Map<String, Class<?>>)getPrivateField("c", entityTypesClass)).put(name, clazz);
			((Map<Class<?>, String>)getPrivateField("d", entityTypesClass)).put(clazz, name);
			((Map<Class<?>, Integer>)getPrivateField("f", entityTypesClass)).put(clazz, 55);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static Object getPrivateField(String fieldName, Class<?> clazz) {
		Object o = null;
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			o = field.get(null);
		}
		catch(NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	private final Spiritcraft plugin;
	
	public CommandListener(Spiritcraft plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		register(); // TODO
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		SpiritPlayer player = plugin.getGame().getPlayerManager().getPlayer(event.getPlayer());
		Action action = event.getAction();
		if(Items.isType(event.getItem(), Material.STONE_AXE)) {
			if(action == Action.LEFT_CLICK_BLOCK) {
				player.getSelection().setLeft(event.getClickedBlock().getLocation());
				event.setCancelled(true);
			}
			else if(action == Action.RIGHT_CLICK_BLOCK) {
				player.getSelection().setRight(event.getClickedBlock().getLocation());
				event.setCancelled(true);
			}
		}
		else if(Items.isType(event.getItem(), Material.STICK)) {
			if(action == Action.LEFT_CLICK_BLOCK) {
				player.getSelection().addBlock(event.getClickedBlock());
				event.setCancelled(true);
			}
			else if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
				if(player.getPlayer().isSneaking()) player.getSelection().addSelection(false);
				else player.getSelection().addSelection(true);
				event.setCancelled(true);
			}
		}
		else if(Items.isType(event.getItem(), Material.STRING)) {
			if(action == Action.LEFT_CLICK_BLOCK) {
				player.getSelection().removeBlock(event.getClickedBlock());
				event.setCancelled(true);
			}
			else if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
				player.getSelection().removeSelection();
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Slime) {
			event.getDamager().sendMessage("good job it worked, it's custom name is " + entity.getCustomName());
			event.setCancelled(true);
		}
	}
	
	private BukkitTask task;
	
	@Com(aliases = { "test" })
	public boolean onTest(BukkitCommandIssuer issuer, String alias, String[] args) {
		Player player = issuer.getPlayer();
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("stop")) {
				if(task != null) task.cancel();
			}
			else if(args[0].equalsIgnoreCase("cloud")) {
				new CloudTask(player.getLocation());
			}
			else if(args[0].equalsIgnoreCase("breath")) {
				new FireBreathTask(plugin.getGame().getPlayerManager().getPlayer(player));
			}
			else if(args[0].equalsIgnoreCase("inventory")) {
				plugin.getGame().getItemManager().openInventory(plugin.getGame().getPlayerManager().getPlayer(player));
			}
			else if(args[0].equalsIgnoreCase("configure")) {
				plugin.getGame().getItemManager().openSelectedInventory(plugin.getGame().getPlayerManager().getPlayer(player), true);
			}
			else if(args[0].equalsIgnoreCase("vinify")) {
				SpiritPlayer p = plugin.getGame().getPlayerManager().getPlayer(player);
				BlockSelection selection = p.getSelection();
				selection.extreme();
				selection.getBlocks().stream().filter(block -> block.getType() == Material.AIR).forEach(block -> setDataForFaces(selection.getLowestY(), block));
			}
			else if(args[0].equalsIgnoreCase("unvinify")) {
				SpiritPlayer p = plugin.getGame().getPlayerManager().getPlayer(player);
				BlockSelection selection = p.getSelection();
				selection.getBlocks().stream().filter(block -> block.getType() == Material.VINE).forEach(block -> block.setType(Material.AIR));
			}
			else if(args[0].equalsIgnoreCase("load")) {
				plugin.getGame().getPlayerManager().getPlayer(player).updatePlayer(player);
			}
			else if(args[0].equalsIgnoreCase("dim")) {
				plugin.getGame().getCurrentArenaData().teleportToOtherDim(plugin.getGame().getPlayerManager().getPlayer(player));
			}
			else if(args[0].equalsIgnoreCase("death")) {
				//DeadBodyManager.spawn(plugin.getGame().getPlayerManager().getPlayer(player));
			}
			else if(args[0].equalsIgnoreCase("purgebodies")) {
				//DeadBodyManager.destroyAll(player.getWorld());
			}
			else if(args[0].equalsIgnoreCase("purge")) {
				player.getWorld().getEntities().forEach(Entity::remove);
			}
		}
		else if(args.length == 5) {
			if(args[0].equalsIgnoreCase("sound")) {
				if(task != null) task.cancel();
				task = BukkitThreads.syncTimer(() -> player.getWorld().playSound(player.getLocation(), Sound.valueOf(args[1].toUpperCase()),
						Float.parseFloat(args[2]), Float.parseFloat(args[3])), 0L, Integer.parseInt(args[4]));
			}
		}
		else if(args.length == 9) {
			if(args[0].equalsIgnoreCase("particle")) { // TODO test dynamic surrounding blocks?
				if(task != null) task.cancel();
				task = BukkitThreads.syncTimer(() -> ParticleEffect.valueOf(args[1].toUpperCase()).send(Collections.singletonList(player.getPlayer()),
						player.getEyeLocation().clone()
								.add(player.getVelocity().clone().multiply(Double.parseDouble(args[7])))
								.add(player.getLocation().getDirection().clone().multiply(Double.parseDouble(args[8]))),
						Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]),
						Double.parseDouble(args[5]), Integer.parseInt(args[6]), 16),
					0L, 1L);
			}
		}
		return true;
	}
	
	private BlockFace[] facesToCheck = { BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH };
	
	private Set<BlockFace> getNearbyFaces(Block block) {
		Set<BlockFace> faces = new HashSet<>();
		for(BlockFace face : facesToCheck) {
			Block relative = block.getRelative(face);
			if(relative.getType().isSolid()) faces.add(face);
		}
		return faces;
	}
	
	private void setDataForFaces(int lowestY, Block block) {
		Set<BlockFace> faces = getNearbyFaces(block);
		if(faces.isEmpty()) {
			if(block.getRelative(BlockFace.UP).getType().isSolid()) block.setTypeIdAndData(Material.VINE.getId(), (byte)0, false);
		}
		else {
			byte data = (byte)addUpFaces(faces);
			block.setTypeIdAndData(Material.VINE.getId(), data, false);
			for(int y = block.getY() - 1; y >= 0; y--) {
				Block down = block.getWorld().getBlockAt(block.getX(), y, block.getZ());
				if(y < lowestY || down.getType() != Material.AIR) break;
				down.setTypeIdAndData(Material.VINE.getId(), data, false);
			}
		}
	}
	
	private int addUpFaces(Set<BlockFace> faces) {
		int sum = 0;
		for(BlockFace face : faces) {
			sum += getAmountForFace(face);
		}
		return sum;
	}
	
	private int getAmountForFace(BlockFace face) {
		switch(face) {
			case SOUTH:
				return 1;
			case WEST:
				return 2;
			case NORTH:
				return 4;
			case EAST:
				return 8;
			default:
				return 0;
		}
	}
	
	@Com(aliases = { "game" })
	public boolean onGame(BukkitCommandIssuer issuer, String alias, String[] args) {
		SpiritPlayer player = plugin.getGame().getPlayerManager().getPlayer(issuer.getPlayer());
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("start")) {
				player.getPlayer().sendMessage("Starting!");
				player.getGame().start();
			}
			else if(args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("end")) {
				player.getPlayer().sendMessage("Stopping!");
				player.getGame().end();
			}
		}
		return true;
	}
	
	@Com(aliases = { "sch" })
	public boolean onSch(BukkitCommandIssuer issuer, String alias, String[] args) {
		SpiritPlayer player = plugin.getGame().getPlayerManager().getPlayer(issuer.getPlayer());
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("name")) {
				player.getCreator().name(args[1].toLowerCase());
				player.getPlayer().sendMessage("Set name to '" + args[1].toLowerCase() + "'.");
			}
			else if(args[0].equalsIgnoreCase("weight")) {
				player.getCreator().weight(Integer.parseInt(args[1]));
				player.getPlayer().sendMessage("Set weight to '" + args[1] + "'.");
			}
			else if(args[0].equalsIgnoreCase("spawn")) {
				Location loc = player.getPlayer().getLocation();
				if(args[1].equalsIgnoreCase("ghost")) {
					player.getCreator().addGhostSpawn(loc);
					player.getPlayer().sendMessage("Added " + loc.getX() + "," + loc.getY() + "," + loc.getZ() + " to ghost spawns.");
				}
				else if(args[1].equalsIgnoreCase("hunter")) {
					player.getCreator().addHunterSpawn(loc);
					player.getPlayer().sendMessage("Added " + loc.getX() + "," + loc.getY() + "," + loc.getZ() + " to hunter spawns.");
				}
			}
		}
		else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("create")) {
				player.getCreator().create();
				player.getPlayer().sendMessage("Created schematic.");
			}
		}
		return true;
	}
	
	@Com(aliases = { "spiritcraft", "spirit", "sc" })
	public boolean onSpiritcraft(BukkitCommandIssuer issuer, String alias, String[] args) {
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("join")) {
				if(!issuer.isPlayer()) return true;
				issuer.getPlayer().teleport(plugin.getGame().getSpawn());
			}
			else if(args[0].equalsIgnoreCase("leave")) {
				if(!issuer.isPlayer()) return true;
				issuer.getPlayer().teleport(new Location(Bukkit.getWorld("lobby"), 0.5, 101.5, 0.5)); // TODO
			}
			return true;
		}
		return false;
	}
	
}
