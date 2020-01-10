package com.kabryxis.spiritcraft.game;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.kabryxis.kabutils.data.Maths;
import com.kabryxis.kabutils.spigot.version.custom.slime.hitbox.CustomHitbox;
import com.kabryxis.kabutils.spigot.version.wrapper.entity.human.WrappedEntityHuman;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

public class DeadBody {
	
	private static final Map<BlockFace, BlockFace[]> nextFaceOrder = new EnumMap<>(BlockFace.class);
	
	static {
		nextFaceOrder.put(BlockFace.NORTH, new BlockFace[] { BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH });
		nextFaceOrder.put(BlockFace.EAST, new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST });
		nextFaceOrder.put(BlockFace.SOUTH, new BlockFace[] { BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH });
		nextFaceOrder.put(BlockFace.WEST, new BlockFace[] { BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST });
	}
	
	private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
	
	private final SpiritPlayer player;
	
	private PacketContainer namedEntitySpawn, bed, entityTeleport, entityTeleportZero, entityDestroy;
	private int entityId;
	private CustomHitbox headHitbox, feetHitbox;
	private Location deathLoc, bedLoc;
	private byte direction;
	
	public DeadBody(SpiritPlayer player) {
		this.player = player;
	}
	
	public World getDeathWorld() {
		return deathLoc.getWorld();
	}
	
	public boolean isSpawned() {
		return deathLoc != null;
	}
	
	public void died() {
		deathLoc = player.getLocation();
		bedLoc = deathLoc.clone();
		bedLoc.setY(0);
		WrappedEntityHuman wrappedEntityHuman = WrappedEntityHuman.newCloneInstance(player.getPlayer());
		entityId = wrappedEntityHuman.getBukkitEntity().getEntityId();
		namedEntitySpawn = protocolManager.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
		namedEntitySpawn.getUUIDs().write(0, player.getUniqueId());
		namedEntitySpawn.getDataWatcherModifier().write(0, new WrappedDataWatcher(wrappedEntityHuman.getDataWatcher()));
		setLocation(namedEntitySpawn);
		bed = protocolManager.createPacket(PacketType.Play.Server.BED);
		bed.getIntegers().write(0, entityId);
		bed.getBlockPositionModifier().write(0, new BlockPosition(bedLoc.getBlockX(), bedLoc.getBlockY(), bedLoc.getBlockZ()));
		entityTeleport = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
		entityTeleport.getBooleans().write(0, true);
		setLocation(entityTeleport);
		entityTeleportZero = entityTeleport.shallowClone();
		entityTeleportZero.getIntegers().write(2, 0);
		entityDestroy = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		entityDestroy.getIntegerArrays().write(0, new int[] { entityId });
		direction = getDirection(player);
		BlockFace face = getBlockFace(direction);
		Location headHitboxLoc = deathLoc.clone().add(face.getModX() / 2.375, -0.35, face.getModZ() / 2.375);
		headHitbox = CustomHitbox.spawn(headHitboxLoc, player.getName());
		feetHitbox = CustomHitbox.spawn(headHitboxLoc.clone().add(face.getModX(), 0, face.getModZ()), player.getName());
		showAll();
	}
	
	public void revive() {
		headHitbox.getBukkitEntity().remove();
		feetHitbox.getBukkitEntity().remove();
		for(Player p : getDeathWorld().getPlayers()) {
			try {
				protocolManager.sendServerPacket(p, entityDestroy);
			} catch(InvocationTargetException e) {
				e.printStackTrace();
			}
			Block bedLocBlock = bedLoc.getBlock();
			p.sendBlockChange(bedLoc, bedLocBlock.getType(), bedLocBlock.getData());
		}
		namedEntitySpawn = null;
		bed = null;
		entityTeleport = null;
		entityTeleportZero = null;
		entityDestroy = null;
		entityId = 0;
		headHitbox = null;
		feetHitbox = null;
		deathLoc = null;
		bedLoc = null;
		direction = (byte)0;
	}
	
	public void showAll() {
		getDeathWorld().getPlayers().forEach(this::show);
	}
	
	public void show(Player player) {
		player.sendBlockChange(bedLoc, Material.BLACK_BED, direction);
		try {
			protocolManager.sendServerPacket(player, namedEntitySpawn);
			protocolManager.sendServerPacket(player, entityTeleportZero);
			protocolManager.sendServerPacket(player, bed);
			protocolManager.sendServerPacket(player, entityTeleport);
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void show(SpiritPlayer player) {
		show(player.getPlayer());
	}
	
	private void setLocation(PacketContainer packet) {
		packet.getIntegers().write(0, entityId).write(1, floor(deathLoc.getX())).write(2, floor(deathLoc.getY())).write(3, floor(deathLoc.getZ()));
		packet.getBytes().write(0, (byte)(deathLoc.getY() * 256F / 360F)).write(1, (byte)0);
	}
	
	private static byte getDirection(SpiritPlayer player) {
		Block block = player.getLocation().getBlock().getRelative(BlockFace.UP);
		BlockFace facing = player.getFacingDirection();
		if(block.getRelative(facing).getType().isSolid()) {
			BlockFace[] order = nextFaceOrder.get(facing);
			for(BlockFace face : order) {
				if(block.getRelative(face).getType().isSolid()) continue;
				return getDirection(face);
			}
		}
		return getDirection(facing);
	}
	
	private static byte getDirection(BlockFace blockFace) {
		switch(blockFace) {
			case EAST:
				return (byte)1;
			case SOUTH:
				return (byte)2;
			case WEST:
				return (byte)3;
			default:
				return (byte)0;
		}
	}
	
	private static BlockFace getBlockFace(byte direction) {
		switch(direction) {
			case 1:
				return BlockFace.EAST;
			case 2:
				return BlockFace.SOUTH;
			case 3:
				return BlockFace.WEST;
			default:
				return BlockFace.NORTH;
		}
	}
	
	private static int floor(double d) {
		return Maths.floor(d * 32D);
	}
	
}
