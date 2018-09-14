package com.kabryxis.spiritcraft.game.a.world;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.weighted.conditional.ConditionalWeighted;
import com.kabryxis.kabutils.spigot.world.Locations;
import com.kabryxis.spiritcraft.game.a.world.schematic.ArenaSchematic;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Arena implements ConditionalWeighted {
	
	private final Set<BlockVector2D> occupiedChunks = new HashSet<>();
	
	private final WorldManager worldManager;
	private final Config data;
	
	private Location location;
	private Vector vectorLocation;
	private int sizeX, sizeY, sizeZ;
	
	public Arena(WorldManager worldManager, Config data) {
		this.worldManager = worldManager;
		this.data = data;
		reload0();
	}
	
	public void reloadData() {
		data.load(config -> reload0());
	}
	
	private void reload0() {
		location = Locations.deserialize(data.get("location", String.class), worldManager);
		vectorLocation = new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		sizeX = data.get("size.x", Integer.class, Integer.MAX_VALUE);
		sizeY = data.get("size.y", Integer.class, 256);
		sizeZ = data.get("size.z", Integer.class, Integer.MAX_VALUE);
		for(int cx = data.get("load.min.cx", Integer.class, (vectorLocation.getBlockX() >> 4) - 3); cx <= data.get("load.max.cx", Integer.class, (vectorLocation.getBlockX() >> 4) + 3); cx++) {
			for(int cz = data.get("load.min.cz", Integer.class, (vectorLocation.getBlockZ() >> 4) - 3); cz <= data.get("load.max.cz", Integer.class, (vectorLocation.getBlockZ() >> 4) + 3); cz++) {
				occupiedChunks.add(new BlockVector2D(cx, cz));
			}
		}
	}
	
	public Set<BlockVector2D> getOccupiedChunks() {
		return new HashSet<>(occupiedChunks);
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Vector getVectorLocation() {
		return vectorLocation;
	}
	
	@Override
	public int getWeight() {
		return data.get("weight", Integer.class, 1000);
	}
	
	@Override
	public boolean test(Object o) {
		if(o instanceof ArenaSchematic) {
			ArenaSchematic schematic = (ArenaSchematic)o;
			Vector dim = Objects.requireNonNull(schematic.getSchematic().getClipboard()).getDimensions();
			return dim.getBlockX() <= sizeX && dim.getBlockY() <= sizeY && dim.getBlockZ() <= sizeZ;
		}
		return true;
	}
	
}
