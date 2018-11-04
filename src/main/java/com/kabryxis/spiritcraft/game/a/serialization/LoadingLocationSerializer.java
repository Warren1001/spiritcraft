package com.kabryxis.spiritcraft.game.a.serialization;

import com.kabryxis.kabutils.spigot.serialization.LocationSerializer;
import com.kabryxis.kabutils.spigot.world.Locations;
import com.kabryxis.kabutils.spigot.world.WorldLoader;
import org.bukkit.Location;

public class LoadingLocationSerializer extends LocationSerializer {
	
	private final WorldLoader worldLoader;
	
	public LoadingLocationSerializer(WorldLoader worldLoader) {
		this.worldLoader = worldLoader;
	}
	
	@Override
	public Location deserialize(Object obj) {
		return Locations.deserialize((String)obj, worldLoader);
	}
	
}
