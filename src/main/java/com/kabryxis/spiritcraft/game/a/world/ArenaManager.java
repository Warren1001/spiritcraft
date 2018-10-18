package com.kabryxis.spiritcraft.game.a.world;

import com.kabryxis.kabutils.data.file.Files;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.kabutils.random.weighted.WeightedRandomArrayList;

import java.io.File;

public class ArenaManager {
	
	private final RandomArrayList<Arena> arenaRegistry = new WeightedRandomArrayList<>(2);
	
	private final WorldManager worldManager;
	private final File folder;
	
	public ArenaManager(WorldManager worldManager, File folder) {
		this.worldManager = worldManager;
		this.folder = folder;
		if(!folder.mkdirs()) Files.forEachFileWithEnding(folder, ".yml", file -> arenaRegistry.add(new Arena(worldManager, new Config(file, true))));
	}
	
	public WorldManager getWorldManager() {
		return worldManager;
	}
	
	public File getFolder() {
		return folder;
	}
	
	public void reloadAll() {
		arenaRegistry.forEach(Arena::reloadData); // TODO handle entries that have been removed or added to disk, reload currently loaded data if necessary
	}
	
	public Arena random() {
		return arenaRegistry.random();
	}
	
}
