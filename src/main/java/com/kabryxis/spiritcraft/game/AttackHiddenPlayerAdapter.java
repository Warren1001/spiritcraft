package com.kabryxis.spiritcraft.game;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

public class AttackHiddenPlayerAdapter extends PacketAdapter {
	
	private final int reach = 4;
	private final double epsilon = 0.0001F;
	
	private final ProtocolManager protocolManager;
	
	public AttackHiddenPlayerAdapter(Plugin plugin) {
		super(plugin, PacketType.Play.Client.ARM_ANIMATION);
		this.protocolManager = ProtocolLibrary.getProtocolManager();
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
	
}
