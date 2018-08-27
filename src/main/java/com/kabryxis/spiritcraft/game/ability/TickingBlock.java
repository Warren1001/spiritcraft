package com.kabryxis.spiritcraft.game.ability;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.kabryxis.kabutils.data.MathHelp;
import com.kabryxis.kabutils.spigot.entity.Entities;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

public class TickingBlock extends FloatingBlock {
	
	private final PacketContainer packetSpawn, packetVelocityFast, packetVelocitySlow, packetDestroy;
	private final int id;
	private final byte data;
	
	private int tick = 0;
	
	public TickingBlock(Location center, Block block) {
		super(block);
		packetSpawn = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		id = block.getType().getId();
		data = block.getData();
		packetSpawn.getIntegers().write(1, MathHelp.floor(loc.getX() * 32.0)).write(2, MathHelp.floor(loc.getY() * 32.0)).write(3, MathHelp.floor(loc.getZ() * 32.0)).write(9, 70).write(10, id + (data << 12));
		packetVelocitySlow = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
		packetVelocityFast = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
		//Vector velocity = loc.clone().subtract(center).toVector().normalize().multiply(0.008);
		Vector velocity = center.clone().subtract(loc).toVector().normalize().multiply(0.008);
		packetVelocitySlow.getIntegers().write(1, (int)(velocity.getX() * 8000.0))
				//.write(2, (int)(Double.max(0.042, velocity.getY() + 0.042) * 8000.0))
				.write(2, (int)((velocity.getY() + 0.04) * 8000.0)).write(3, (int)(velocity.getZ() * 8000.0));
		//0.03999999910593033
		velocity.multiply(-1);
		packetVelocityFast.getIntegers().write(1, (int)(velocity.getX() * /*(new Random().nextInt(20) + 6) * 10*/200 * 8000.0)).write(2, (int)((velocity.getY() + 0.04) * /*(new Random().nextInt(20) + 6)*/50 *
				8000.0)).write(3, (int)(velocity.getZ() * /*(new Random().nextInt(20) + 6) * 10*/200 * 8000.0));
		packetDestroy = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
	}
	
	@Override
	public void start() {
		int id = Entities.nextEntityId();
		packetSpawn.getIntegers().write(0, id);
		packetVelocitySlow.getIntegers().write(0, id);
		packetVelocityFast.getIntegers().write(0, id);
		packetDestroy.getIntegerArrays().write(0, new int[] {id}); // TODO create one global packet that has an array of all ids
		super.start();
		sendPacket(packetSpawn);
	}
	
	@Override
	public void end() {
		tick = 0;
		sendPacket(packetDestroy);
		super.end();
	}
	
	private void sendPacket(PacketContainer packet) {
		loc.getWorld().getPlayers().forEach(player -> {
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
			} catch(InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}
	
	public void tick() {
		//if(tick <= 5) sendPacket(packetVelocityFast);
		//else sendPacket(packetVelocitySlow);
		sendPacket(packetVelocitySlow);
		tick++;
	}
	
	public void fast() {
		sendPacket(packetVelocityFast);
	}
	
}
