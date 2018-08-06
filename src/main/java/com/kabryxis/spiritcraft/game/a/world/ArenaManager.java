package com.kabryxis.spiritcraft.game.a.world;

import com.boydti.fawe.FaweCache;
import com.kabryxis.kabutils.data.file.FileEndingFilter;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.weighted.WeightedRandomArrayList;
import com.kabryxis.kabutils.random.weighted.conditional.SelfConditionalWeightedRandomArrayList;
import com.sk89q.worldedit.blocks.BaseBlock;

import java.io.File;

public class ArenaManager {
	
	public static final BaseBlock AIR = FaweCache.getBlock(0, 0);
	
	private final WeightedRandomArrayList<Arena> arenaRegistry = new SelfConditionalWeightedRandomArrayList<>(2);
	
	private final WorldManager worldManager;
	private final File folder;
	
	public ArenaManager(WorldManager worldManager, File folder) {
		this.worldManager = worldManager;
		this.folder = folder;
		folder.mkdirs();
		File[] files = folder.listFiles(new FileEndingFilter(".yml"));
		if(files != null) {
			for(File file : files) {
				Config data = new Config(file);
				data.loadSync();
				arenaRegistry.add(new Arena(worldManager, data));
			}
		}
	}
	
	public WorldManager getWorldManager() {
		return worldManager;
	}
	
	public File getFolder() {
		return folder;
	}
	
	public void reloadAll() { // TODO handle entries that have been removed or added to disk, reload currently loaded data if necessary
		arenaRegistry.forEach(Arena::reload);
	}
	
	public Arena random() {
		return arenaRegistry.random();
	}
	
}
