package com.kabryxis.spiritcraft.game;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

public class AttackHiddenPlayerAdapter extends PacketAdapter {
	
	private final int reach = 4;
	private final double epsilon = 0.0001F;
	
	private final ProtocolManager protocolManager;
	
	public AttackHiddenPlayerAdapter(Plugin plugin) {
		super(plugin, PacketType.Play.Client.ARM_ANIMATION);
		protocolManager = ProtocolLibrary.getProtocolManager();
	}
	
	@Override
	public void onPacketReceiving(PacketEvent event) {
		Player observer = event.getPlayer();
		Location observerPos = observer.getEyeLocation();
		Vector3D observerStart = new Vector3D(observerPos);
		Vector3D observerEnd = observerStart.add(new Vector3D(observerPos.getDirection()).multiply(reach));
		Player hit = null;
		for(Player target : protocolManager.getEntityTrackers(observer)) {
			if(observer.canSee(target)) continue;
			Vector3D targetPos = new Vector3D(target.getLocation());
			if(hasIntersection(observerStart, observerEnd, targetPos.add(-0.5, 0, -0.5), targetPos.add(0.5, 1.67, 0.5)) &&
					(hit == null || hit.getLocation().distanceSquared(observerPos) > target.getLocation().distanceSquared(observerPos))) hit = target;
		}
		if(hit != null) {
			PacketContainer useEntity = protocolManager.createPacket(PacketType.Play.Client.USE_ENTITY, false);
			useEntity.getIntegers().write(0, hit.getEntityId());
			useEntity.getEntityUseActions().write(0, EnumWrappers.EntityUseAction.ATTACK);
			try {
				protocolManager.recieveClientPacket(event.getPlayer(), useEntity);
			} catch(IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
		Vector3D d = p2.subtract(p1).multiply(0.5);
		Vector3D e = max.subtract(min).multiply(0.5);
		Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
		Vector3D ad = d.abs();
		return Math.abs(c.x) <= e.x + ad.x && Math.abs(c.y) <= e.y + ad.y && Math.abs(c.z) <= e.z + ad.z &&
				Math.abs(d.y * c.z - d.z * c.y) <= e.y * ad.z + e.z * ad.y + epsilon &&
				Math.abs(d.z * c.x - d.x * c.z) <= e.z * ad.x + e.x * ad.z + epsilon &&
				Math.abs(d.x * c.y - d.y * c.x) <= e.x * ad.y + e.y * ad.x + epsilon;
	}
	
	public static class Vector3D {
		
		/**
		 * Represents the null (0, 0, 0) origin.
		 */
		public static final Vector3D ORIGIN = new Vector3D(0, 0, 0);
		
		// Use protected members, like Bukkit
		public final double x;
		public final double y;
		public final double z;
		
		/**
		 * Construct an immutable 3D vector.
		 */
		public Vector3D(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		/**
		 * Construct an immutable floating point 3D vector from a location object.
		 * @param location - the location to copy.
		 */
		public Vector3D(Location location) {
			this(location.toVector());
		}
		
		/**
		 * Construct an immutable floating point 3D vector from a mutable Bukkit vector.
		 * @param vector - the mutable real Bukkit vector to copy.
		 */
		public Vector3D(Vector vector) {
			Validate.notNull(vector, "vector cannot be null");
			this.x = vector.getX();
			this.y = vector.getY();
			this.z = vector.getZ();
		}
		
		/**
		 * Convert this instance to an equivalent real 3D vector.
		 * @return Real 3D vector.
		 */
		public Vector toVector() {
			return new Vector(x, y, z);
		}
		
		/**
		 * Adds the current vector and a given position vector, producing a result vector.
		 * @param other - the other vector.
		 * @return The new result vector.
		 */
		public Vector3D add(Vector3D other) {
			Validate.notNull(other, "other cannot be null");
			return new Vector3D(x + other.x, y + other.y, z + other.z);
		}
		
		/**
		 * Adds the current vector and a given vector together, producing a result vector.
		 * @return The new result vector.
		 */
		public Vector3D add(double x, double y, double z) {
			return new Vector3D(this.x + x, this.y + y, this.z + z);
		}
		
		/**
		 * Substracts the current vector and a given vector, producing a result position.
		 * @param other - the other position.
		 * @return The new result position.
		 */
		public Vector3D subtract(Vector3D other) {
			Validate.notNull(other, "other cannot be null");
			return new Vector3D(x - other.x, y - other.y, z - other.z);
		}
		
		/**
		 * Substracts the current vector and a given vector together, producing a result vector.
		 * @return The new result vector.
		 */
		public Vector3D subtract(double x, double y, double z) {
			return new Vector3D(this.x - x, this.y - y, this.z - z);
		}
		
		/**
		 * Multiply each dimension in the current vector by the given factor.
		 * @param factor - multiplier.
		 * @return The new result.
		 */
		public Vector3D multiply(int factor) {
			return new Vector3D(x * factor, y * factor, z * factor);
		}
		
		/**
		 * Multiply each dimension in the current vector by the given factor.
		 * @param factor - multiplier.
		 * @return The new result.
		 */
		public Vector3D multiply(double factor) {
			return new Vector3D(x * factor, y * factor, z * factor);
		}
		
		/**
		 * Divide each dimension in the current vector by the given divisor.
		 * @param divisor - the divisor.
		 * @return The new result.
		 */
		public Vector3D divide(int divisor) {
			Validate.isTrue(divisor != 0, "Cannot divide by 0");
			return new Vector3D(x / divisor, y / divisor, z / divisor);
		}
		
		/**
		 * Divide each dimension in the current vector by the given divisor.
		 * @param divisor - the divisor.
		 * @return The new result.
		 */
		public Vector3D divide(double divisor) {
			Validate.isTrue(divisor != 0, "Cannot divide by 0");
			return new Vector3D(x / divisor, y / divisor, z / divisor);
		}
		
		/**
		 * Retrieve the absolute value of this vector.
		 * @return The new result.
		 */
		public Vector3D abs() {
			return new Vector3D(Math.abs(x), Math.abs(y), Math.abs(z));
		}
		
		@Override
		public String toString() {
			return String.format("[x: %s, y: %s, z: %s]", x, y, z);
		}
		
	}
	
}
