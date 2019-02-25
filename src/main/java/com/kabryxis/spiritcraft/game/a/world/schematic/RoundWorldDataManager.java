package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.kabutils.random.weighted.WeightedRandomArrayList;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;

import java.io.File;

public class RoundWorldDataManager {
	
	private final RandomArrayList<RoundWorldDataContainer> dataContainers = new WeightedRandomArrayList<>();
	
	private final WorldManager worldManager;
	private final File folder;
	
	public RoundWorldDataManager(WorldManager worldManager, File folder) {
		this.worldManager = worldManager;
		this.folder = folder;
		if(!folder.exists()) {
			// TODO
		}
		Config.forEachConfig(folder, data -> dataContainers.add(new RoundWorldDataContainer(worldManager, data, folder)));
		//Files.forEachDirectory(folder, dir -> dataContainers.add(new RoundWorldDataContainer(worldManager, new Config(new File(dir, "master.yml"), true), dir)));
	}
	
	public RoundWorldData create() {
		return dataContainers.random().create();
	}
	
}
