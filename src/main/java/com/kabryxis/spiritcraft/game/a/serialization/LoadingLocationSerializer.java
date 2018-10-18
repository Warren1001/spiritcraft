package com.kabryxis.spiritcraft.game.a.serialization;

import com.kabryxis.kabutils.spigot.serialization.LocationSerializer;
import com.kabryxis.kabutils.spigot.world.WorldLoader;
import org.bukkit.Location;

public class LoadingLocationSerializer extends LocationSerializer {
	
	private final WorldLoader worldLoader;
	
	public LoadingLocationSerializer(WorldLoader worldLoader) {
		this.worldLoader = worldLoader;
	}
	
	@Override
	public Object deserialize(String string) {
		String[] args = string.split(",");
		return args.length == 4 ? new Location(worldLoader.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3])) :
				new Location(worldLoader.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));
	}
	
}
