package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.kabryxis.kabutils.data.Lists;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.weighted.Weighted;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;

import java.io.File;
import java.util.List;

public class RoundWorldDataContainer implements Weighted {
	
	private final WorldManager worldManager;
	private final Config data;
	private final File folder;
	
	private SchematicDataContainer schDataContainer;
	
	public RoundWorldDataContainer(WorldManager worldManager, Config data, File folder) {
		this.worldManager = worldManager;
		this.data = data;
		this.folder = folder;
		load();
	}
	
	public void load() {
		Object schObj = data.get("schematic");
		if(schObj == null) schDataContainer = new SimpleSchematicDataContainer(worldManager, data, data.getName());
		else if(schObj instanceof String) schDataContainer = new SimpleSchematicDataContainer(worldManager, data);
		else if(schObj instanceof List) schDataContainer = new ComplexSchematicDataContainer(worldManager, data, Lists.convert((List)schObj, Object::toString));
		else throw new IllegalArgumentException("cannot load " + schObj + " from schematic master file."); // TODO
	}
	
	public RoundWorldData create() {
		return schDataContainer.create(); // TODO
	}
	
	@Override
	public int getWeight() {
		return data.getInt("weight", 1000);
	}
	
}
