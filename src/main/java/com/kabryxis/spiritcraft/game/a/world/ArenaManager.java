package com.kabryxis.spiritcraft.game.a.world;

import com.kabryxis.kabutils.data.file.KFiles;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.conditional.ConditionalRandomArrayList;
import com.kabryxis.kabutils.random.weighted.conditional.ObjectPredicate;

import java.io.File;
import java.util.stream.Stream;

public class ArenaManager {
	
	private final ConditionalRandomArrayList<Arena> arenaRegistry = new ConditionalRandomArrayList<>(2);
	
	private final WorldManager worldManager;
	private final File folder;
	
	public ArenaManager(WorldManager worldManager, File folder) {
		this.worldManager = worldManager;
		this.folder = folder;
		folder.mkdirs();
		if(folder.exists()) KFiles.forEachFileWithEnding(folder, ".yml", file -> arenaRegistry.add(new Arena(worldManager, new Config(file, true))));
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
	
	public Arena random(ObjectPredicate obj) { // TODO
		return arenaRegistry.random(obj);
	}
	
	public Arena random(String... names) {
		Stream<String> stream = Stream.of(names);
		return random(obj -> stream.anyMatch(s -> s.equals(((Arena)obj).getName())));
	}
	
}
