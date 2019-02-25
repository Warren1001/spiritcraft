package com.kabryxis.spiritcraft.game.a.world;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.weighted.Weighted;
import com.kabryxis.kabutils.spigot.world.ImmutableLocation;
import com.sk89q.worldedit.*;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class Arena implements Weighted {
	
	private final Set<Vector2D> occupiedChunks = new HashSet<>();
	
	private final WorldManager worldManager;
	private final Config data;
	
	private boolean dynamic = false;
	private Location location;
	private Vector vectorLocation;
	private EditSession editSession;
	private int sizeX, sizeY, sizeZ;
	
	public Arena(WorldManager worldManager, Config data) {
		this.worldManager = worldManager;
		this.data = data;
		reloadData();
	}
	
	public void reloadData() {
		dynamic = data.getBoolean("dynamic", false);
		location = new ImmutableLocation(data.getCustom("location", Location.class));
		vectorLocation = new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		editSession = worldManager.getEditSession(location.getWorld());
		sizeX = data.getInt("size.x", Integer.MAX_VALUE);
		sizeY = data.getInt("size.y", location.getWorld().getMaxHeight());
		sizeZ = data.getInt("size.z", Integer.MAX_VALUE);
		int cx = vectorLocation.getBlockX() >> 4;
		int cz = vectorLocation.getBlockZ() >> 4;
		int micx = cx - data.getInt("load.min.cx", 3);
		int macx = cx + data.getInt("load.max.cx", 3);
		int micz = cz - data.getInt("load.min.cz", 3);
		int macz = cz + data.getInt("load.max.cz", 3);
		for(cx = micx; cx <= macx; cx++) {
			for(cz = micz; cz <= macz; cz++) {
				occupiedChunks.add(new BlockVector2D(cx, cz));
			}
		}
	}
	
	public String getName() {
		return data.getName();
	}
	
	public boolean isDynamic() {
		return dynamic;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Vector getVectorLocation() {
		return vectorLocation;
	}
	
	public EditSession getEditSession() {
		return editSession;
	}
	
	public Set<Vector2D> getOccupiedChunks() {
		return new HashSet<>(occupiedChunks);
	}
	
	public boolean fits(Vector dimensions) {
		return dimensions.getBlockX() <= sizeX && dimensions.getBlockY() <= sizeY && dimensions.getBlockZ() <= sizeZ;
	}
	
	@Override
	public int getWeight() {
		return data.getInt("weight", 1000);
	}
	
}
