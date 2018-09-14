package com.kabryxis.spiritcraft.deprecated;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.weighted.conditional.ConditionalWeighted;
import com.kabryxis.kabutils.spigot.world.Locations;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;

public class Arena implements ConditionalWeighted {
	
	private final WorldManager worldManager;
	private final Config data;
	
	private DimInfo normalDimInfo, spiritDimInfo;
	
	public Arena(WorldManager worldManager, Config data) {
		this.worldManager = worldManager;
		this.data = data;
		this.normalDimInfo = new DimInfo(worldManager, Locations.deserialize(data.get("location.normal", String.class), worldManager));
		this.spiritDimInfo = new DimInfo(worldManager, Locations.deserialize(data.get("location.spirit", String.class), worldManager));
	}
	
	public void reload() {
		data.load(config -> {
			normalDimInfo = new DimInfo(worldManager, Locations.deserialize(config.get("location.normal", String.class), worldManager));
			spiritDimInfo = new DimInfo(worldManager, Locations.deserialize(config.get("location.spirit", String.class), worldManager));
		});
	}
	
	public void load() {
		/*worldManager.loadChunks(this, normalDimInfo.getLocation());
		worldManager.loadChunks(this, spiritDimInfo.getLocation());*/
	}
	
	public void unload() {
		worldManager.unloadChunks(this);
	}
	
	public DimInfo getNormalDimInfo() {
		return normalDimInfo;
	}
	
	public DimInfo getSpiritDimInfo() {
		return spiritDimInfo;
	}
	
	@Override
	public int getWeight() {
		return data.get("weight", Integer.class, 1000, true);
	}
	
	@Override
	public boolean test(Object o) {
		if(o instanceof Schematic) {
			Schematic schematic = (Schematic)o;
			//System.out.println("arena " + data.getName() + " is compatible with " + schematic.getName() + " schematic");
			return true; // TODO
		}
		return true;
	}
	
}
