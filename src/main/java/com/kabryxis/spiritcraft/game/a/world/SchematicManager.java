package com.kabryxis.spiritcraft.game.a.world;

import com.kabryxis.spiritcraft.game.Schematic;
import com.kabryxis.spiritcraft.game.SchematicLoader;
import com.kabryxis.kabutils.data.file.FileEndingFilter;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.weighted.conditional.ConditionalWeightedRandomArrayList;
import com.kabryxis.kabutils.spigot.world.schematic.BlockSelection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class SchematicManager {

	private final Map<String, Schematic> normalSchematics = new HashMap<>();
	private final Map<String, Schematic> spiritSchematics = new HashMap<>();
	private final ConditionalWeightedRandomArrayList<Schematic> schematicRegistry = new ConditionalWeightedRandomArrayList<>(2);
	
	private final File folder;
	private final File spiritFolder;
	private final SchematicLoader loader;
	
	public SchematicManager(File folder) {
		this.folder = folder;
		this.spiritFolder = new File(folder, "spirit");
		this.loader = new SchematicLoader(this);
		spiritFolder.mkdirs();
		File[] files = folder.listFiles(new FileEndingFilter(".sch"));
		if(files != null) {
			for(File schFile : files) {
				String schName = schFile.getName().split("\\.")[0];
				File dataFile = new File(schFile.getParent(), schName + "-data.yml");
				if(!dataFile.exists()) {
					System.out.println("No data found for schematic '" + schName + "', skipping.");
					continue;
				}
				File spiritSchFile = new File(spiritFolder, schFile.getName());
				if(!spiritSchFile.exists()) {
					System.out.println("No spirit schematic found for schematic '" + schName + "', skipping.");
					continue;
				}
				File spiritDataFile = new File(spiritFolder, schName + "-data.yml");
				if(!spiritDataFile.exists()) {
					System.out.println("No data found for spirit schematic '" + schName + "', skipping.");
					continue;
				}
				add(loader.loadSync(schFile, new Config(dataFile)));
				spiritSchematics.put(schName, loader.loadSync(spiritSchFile, new Config(spiritDataFile)));
			}
		}
	}
	
	public File getFolder() {
		return folder;
	}
	
	public void reloadAll() {
		// TODO
	}
	
	public void load(File schFile, Config data) {
		loader.load(schFile, data, this::add);
	}
	
	public void create(String name, BlockSelection selection, Config data) {
		add(loader.loadSync(name, selection, data));
	}
	
	public void add(Schematic schematic) {
		normalSchematics.put(schematic.getName(), schematic);
		schematicRegistry.add(schematic);
	}
	
	public Schematic random(Predicate<Object>... objs) {
		return schematicRegistry.random(objs);
	}
	
	public Schematic get(String name) {
		return normalSchematics.get(name);
	}
	
	public Schematic getSpirit(String name) {
		return spiritSchematics.get(name);
	}

}
