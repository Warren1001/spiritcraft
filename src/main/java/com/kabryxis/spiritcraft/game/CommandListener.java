package com.kabryxis.spiritcraft.game;

import com.boydti.fawe.FaweCache;
import com.boydti.fawe.util.EditSessionBuilder;
import com.kabryxis.kabutils.command.Com;
import com.kabryxis.kabutils.spigot.command.BukkitCommandIssuer;
import com.kabryxis.spiritcraft.Spiritcraft;
import com.kabryxis.spiritcraft.game.ability.FireBreathTask;
import com.kabryxis.spiritcraft.game.ability.WorldEndTask;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class CommandListener implements Listener {
	
	private final Spiritcraft plugin;
	
	public CommandListener(Spiritcraft plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) && light) {
			Block block = event.getClickedBlock();
			event.getPlayer().sendMessage(block.getType().name() + ":" + block.getData() + " (" + block.getType().getId() + ":" + block.getData() + ")");
			if(block.getType() == Material.REDSTONE_LAMP_OFF) {
				EditSession editSession = new EditSessionBuilder(block.getWorld().getName()).fastmode(true).build();
				try {
					editSession.setBlock(new BlockVector(block.getX(), block.getY(), block.getZ()), FaweCache.getBlock(Material.REDSTONE_LAMP_ON.getId(), 0));
					editSession.flushQueue();
				} catch(MaxChangedBlocksException e) {
					e.printStackTrace();
				}
				event.getPlayer().sendMessage("on");
			}
			else if(block.getType() == Material.REDSTONE_LAMP_ON) {
				block.setType(Material.REDSTONE_LAMP_OFF, false);
				event.getPlayer().sendMessage("off");
			}
			event.setCancelled(true);
		}
	}
	
	private Location explodeLoc;
	
	private boolean checkRelationalLoc = false; // TODO origin is literal origin, schematics dont seem to record an offset
	private WorldEndTask worldEndTask;
	
	private BukkitTask task;
	private Location distanceLoc;
	private BlockFace[] facesToCheck = {BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};
	private boolean light = false;
	
	@Com
	public boolean test(BukkitCommandIssuer issuer, String alias, String[] args) {
		Player player = issuer.getPlayer();
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("stop")) {
				if(task != null) task.cancel();
				if(worldEndTask != null) worldEndTask.cancel();
			}
			else if(args[0].equalsIgnoreCase("cloud")) {
				//new CloudTask(player.getLocation());
			}
			else if(args[0].equalsIgnoreCase("breath")) {
				new FireBreathTask(plugin.getGame().getPlayerManager().getPlayer(player));
			}
			else if(args[0].equalsIgnoreCase("inventory")) {
				plugin.getGame().getItemManager().openInventory(plugin.getGame().getPlayerManager().getPlayer(player));
				/*PacketContainer titlePacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_WINDOW);
				titlePacket.getIntegers().write(0, -1).write(1, 36);
				titlePacket.getStrings().write(0, "minecraft:container");
				titlePacket.getChatComponents().write(0, WrappedChatComponent.fromText("This is a test"));
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(player, titlePacket);
				} catch(InvocationTargetException e) {
					e.printStackTrace();
				}*/
			}
			else if(args[0].equalsIgnoreCase("configure")) {
				plugin.getGame().getItemManager().openSelectedInventory(plugin.getGame().getPlayerManager().getPlayer(player), true);
			}
			else if(args[0].equalsIgnoreCase("particles")) {
			
			}
			else if(args[0].equalsIgnoreCase("vinify")) {
				SpiritPlayer p = plugin.getGame().getPlayerManager().getPlayer(player);
				EditSession editSession = new EditSessionBuilder(p.getWorld().getName()).fastmode(true).build();
				Region selection = p.getSelection();
				selection.forEach(pos -> {
					if(editSession.getLazyBlock(pos).getId() == 0) setDataForFaces(selection.getMinimumPoint().getBlockY(), p.getWorld(), editSession, pos);
				});
				editSession.flushQueue();
			}
			else if(args[0].equalsIgnoreCase("unvinify")) {
				SpiritPlayer p = plugin.getGame().getPlayerManager().getPlayer(player);
				EditSession editSession = new EditSessionBuilder(p.getWorld().getName()).fastmode(true).build();
				p.getSelection().forEach(pos -> {
					if(editSession.getLazyBlock(pos).getId() == Material.VINE.getId()) {
						try {
							editSession.setBlock(pos, FaweCache.getBlock(0, 0));
						} catch(MaxChangedBlocksException e) {
							e.printStackTrace();
						}
					}
				});
				editSession.flushQueue();
			}
			else if(args[0].equalsIgnoreCase("light")) {
				light = !light;
				player.sendMessage("light: " + light);
			}
			else if(args[0].equalsIgnoreCase("purge")) {
				player.getWorld().getEntities().stream().filter(entity -> entity.getType() != EntityType.PLAYER).forEach(Entity::remove);
			}
			else if(args[0].equalsIgnoreCase("pull")) {
				if(task != null) task.cancel();
				Location loc = plugin.getGame().getSpawn().clone().add(0, 1.5, 0);
				/*task = BukkitThreads.syncTimer(() -> {
					Location playerLoc = player.getLocation();
					double dist = loc.distanceSquared(playerLoc);
					if(dist > 128.0) return;
					Vector velocity = loc.clone().subtract(playerLoc).toVector().normalize();
					double motX = velocity.getX(), motY = velocity.getY(), motZ = velocity.getZ();
					boolean motXN = motX < 0.0, motZN = motZ < 0.0;
					if(motX != 0.0) {
						motX = Math.abs(motX) - (0.974 * (0.974 / Math.abs(motX)));
						motX = Math.min(motX, 1.0);
						if(motXN) motX *= -1;
					}
					if(motZ != 0.0) {
						motZ = Math.abs(motZ) - (0.974 * (0.974 / Math.abs(motZ)));
						motZ = Math.min(motZ, 1.0);
						if(motZN) motZ *= -1;
					}
					motX /= 10;
					motZ /= 10;
					velocity.setX(motX);
					velocity.setY(0); // TODO
					velocity.setZ(motZ);
					System.out.println(velocity);
					player.setVelocity(player.getVelocity().add(velocity));
				}, 0L, 1L);*/
				/*task = BukkitThreads.syncTimer(() -> {
					Location playerLoc = player.getLocation();
					double dist = loc.distanceSquared(playerLoc);
					if(dist > 128.0) return;
					double cx = loc.getX();
					double cz = loc.getZ();
					double px = playerLoc.getX();
					double pz = playerLoc.getZ();
					double motX = cx - px;
					double motZ = cz - pz;
					double strength = 128.0 / dist;
					if(dist <= 16.0) {
						strength = 16.0 / dist;
					}
					System.out.println(dist + ": " + motX + "," + motZ + " * " + strength + " = " + (motX * strength) + "," + (motZ * strength));
					//Vector velocity = loc.clone().subtract(playerLoc).toVector();
					/*velocity.setX(((int)(velocity.getX() * 10000)) / 10000.0);
					velocity.setY(((int)(velocity.getY() * 10000)) / 10000.0);
					velocity.setZ(((int)(velocity.getZ() * 10000)) / 10000.0);
					if(Math.abs(velocity.getX()) < 0.001) velocity.setX(0);
					if(Math.abs(velocity.getY()) < 0.001) velocity.setY(0);
					if(Math.abs(velocity.getZ()) < 0.001) velocity.setZ(0);
					//System.out.println(dist + ": " + velocity + " - " + velocity.multiply(strength));
					player.setVelocity(player.getVelocity().add(new Vector(motX * strength, 0, motZ * strength)));
				}, 0L, 1L);*/
			}
			else if(args[0].equalsIgnoreCase("worldend")) {
				if(worldEndTask != null) worldEndTask.cancel();
				worldEndTask = new WorldEndTask(plugin.getGame(), player.getLocation());
			}
			else if(args[0].equalsIgnoreCase("togglerelational")) {
				checkRelationalLoc = !checkRelationalLoc;
				System.out.println("checking relational loc: " + checkRelationalLoc);
			}
			else if(args[0].equalsIgnoreCase("setslot")) {
				player.getInventory().setHeldItemSlot(0);
			}
			else if(args[0].equalsIgnoreCase("overloadsound")) {
				/*task = BukkitThreads.syncTimer(new BukkitRunnable() {
					
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
							System.out.println("soundInterval: " + soundInterval);
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
					
				}, 0L, 1L);*/
			}
			else if(args[0].equalsIgnoreCase("glassbreak")) {
				SpiritPlayer spiritPlayer = plugin.getGame().getPlayerManager().getPlayer(player);
				Region selection = spiritPlayer.getSelection();
				selection.forEach(bv -> {
					Block block = spiritPlayer.getWorld().getBlockAt(bv.getBlockX(), bv.getBlockY(), bv.getBlockZ());
					if(block.getType() == Material.REDSTONE_LAMP_OFF || block.getType() == Material.REDSTONE_LAMP_ON) {
						//BukkitThreads.syncLater(() -> block.getWorld().playSound(block.getLocation(), Sound.GLASS, 10F, 0.7F), new Random().nextInt(3));
					}
				});
			}
		}
		else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("speed")) {
				player.setWalkSpeed(Float.parseFloat(args[1]));
			}
			else if(args[0].equalsIgnoreCase("distance")) {
				if(args[1].equalsIgnoreCase("set")) {
					distanceLoc = player.getLocation();
				}
				else if(args[1].equalsIgnoreCase("check")) {
					player.sendMessage(String.valueOf(player.getLocation().distanceSquared(distanceLoc)));
				}
				else if(args[1].equalsIgnoreCase("spamcheck")) {
					if(task != null) task.cancel();
					//task = BukkitThreads.syncTimer(() -> player.sendMessage(String.valueOf(player.getLocation().distanceSquared(distanceLoc))), 0L, 2L);
				}
			}
			else if(args[0].equalsIgnoreCase("spectate")) {
				Player otherPlayer = Bukkit.getPlayer(args[1]);
				if(otherPlayer == null) {
					player.sendMessage("Could not find player '" + args[1] + "'.");
					return true;
				}
				player.setSpectatorTarget(otherPlayer);
			}
		}
		else if(args.length == 5) {
			if(args[0].equalsIgnoreCase("sound")) {
				if(task != null) task.cancel();
				/*task = BukkitThreads.syncTimer(() -> player.playSound(player.getLocation(), Sound.valueOf(args[1].toUpperCase()),
						Float.parseFloat(args[2]), Float.parseFloat(args[3])), 0L, Integer.parseInt(args[4]));*/
			}
		}
		else if(args.length == 9) {
			if(args[0].equalsIgnoreCase("particle")) { // TODO test dynamic surrounding blocks?
				if(task != null) task.cancel();
				/*task = BukkitThreads.syncTimer(() -> ParticleEffect.valueOf(args[1].toUpperCase()).send(Collections.singletonList(player.getPlayer()), player.getEyeLocation().clone()
					.add(player.getVelocity().clone().multiply(Double.parseDouble(args[7]))).add(player.getLocation().getDirection().clone()
					.multiply(Double.parseDouble(args[8]))), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Integer.parseInt(args[6]), 16),
					0L, 1L);*/
			}
		}
		return true;
	}
	
	private Set<BlockFace> getNearbyFaces(Block block) {
		Set<BlockFace> faces = new HashSet<>();
		for(BlockFace face : facesToCheck) {
			Block relative = block.getRelative(face);
			if(relative.getType().isSolid()) faces.add(face);
		}
		return faces;
	}
	
	private void setDataForFaces(int lowestY, World world, EditSession editSession, com.sk89q.worldedit.Vector pos) {
		Block block = world.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
		Set<BlockFace> faces = getNearbyFaces(block);
		if(faces.isEmpty()) {
			if(block.getRelative(BlockFace.UP).getType().isSolid()) editSession.setBlock(pos, FaweCache.getBlock(Material.VINE.getId(), 0), true);
		}
		else {
			int data = addUpFaces(faces);
			editSession.setBlock(pos, FaweCache.getBlock(Material.VINE.getId(), data), true);
			for(int y = block.getY() - 1; y >= lowestY; y--) {
				Block down = block.getWorld().getBlockAt(block.getX(), y, block.getZ());
				if(down.getType() != Material.AIR) break;
				editSession.setBlock(pos, FaweCache.getBlock(Material.VINE.getId(), data), true);
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
	
	@Com
	public boolean tpw(BukkitCommandIssuer issuer, String alias, String[] args) {
		if(args.length == 1) {
			World world = Bukkit.getWorld(args[0]);
			if(world == null) world = new WorldCreator(args[0]).createWorld();
			issuer.getPlayer().teleport(world.getSpawnLocation());
		}
		else if(args.length == 4) {
			World world = Bukkit.getWorld(args[0]);
			if(world == null) world = new WorldCreator(args[0]).createWorld();
			issuer.getPlayer().teleport(new Location(world, Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3])));
		}
		return true;
	}
	
	@Com
	public boolean game(BukkitCommandIssuer issuer, String alias, String[] args) {
		SpiritPlayer player = plugin.getGame().getPlayerManager().getPlayer(issuer.getPlayer());
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("start")) {
				player.getPlayer().sendMessage("Starting!");
				player.getGame().start();
			}
			else if(args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("end")) {
				player.getPlayer().sendMessage("Stopping!");
				player.getGame().end(true);
			}
		}
		return true;
	}
	
	@Com
	public boolean schdata(BukkitCommandIssuer issuer, String alias, String[] args) {
		SpiritPlayer player = plugin.getGame().getPlayerManager().getPlayer(issuer.getPlayer());
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("name")) {
				player.getDataCreator().name(args[1].toLowerCase());
				player.getPlayer().sendMessage("Set name to '" + args[1].toLowerCase() + "'.");
			}
			else if(args[0].equalsIgnoreCase("weight")) {
				player.getDataCreator().weight(Integer.parseInt(args[1]));
				player.getPlayer().sendMessage("Set weight to '" + args[1] + "'.");
			}
			else if(args[0].equalsIgnoreCase("spawn")) {
				Location loc = player.getPlayer().getLocation();
				if(args[1].equalsIgnoreCase("ghost")) {
					player.getDataCreator().addGhostSpawn(loc);
					player.getPlayer().sendMessage("Added " + loc.getX() + "," + loc.getY() + "," + loc.getZ() + " to ghost spawns.");
				}
				else if(args[1].equalsIgnoreCase("hunter")) {
					player.getDataCreator().addHunterSpawn(loc);
					player.getPlayer().sendMessage("Added " + loc.getX() + "," + loc.getY() + "," + loc.getZ() + " to hunter spawns.");
				}
			}
		}
		else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("create")) {
				player.getDataCreator().create();
				player.getPlayer().sendMessage("Created schematic.");
			}
		}
		return true;
	}
	
	@Com(aliases = {"spirit", "sc"})
	public boolean spiritcraft(BukkitCommandIssuer issuer, String alias, String[] args) {
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
