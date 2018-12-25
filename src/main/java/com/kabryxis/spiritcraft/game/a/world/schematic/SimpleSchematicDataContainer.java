package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.object.schematic.Schematic;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;

import java.io.File;
import java.io.IOException;

public class SimpleSchematicDataContainer implements SchematicDataContainer {
	
	protected final WorldManager worldManager;
	protected final Schematic schematic;
	protected final Config data;
	
	public SimpleSchematicDataContainer(WorldManager worldManager, Schematic schematic) {
		this.worldManager = worldManager;
		this.schematic = schematic;
		data = null;
	}
	
	public SimpleSchematicDataContainer(WorldManager worldManager, File folder, String name) {
		this.worldManager = worldManager;
		name = name.replace('/', File.separatorChar).replace('\\', File.separatorChar);
		File file = new File(folder, name + ".yml");
		if(file.exists()) {
			data = new Config(file, true);
			file = new File(file.getParent(), data.get("schematic", name) + ".sch");
		}
		else {
			data = null;
			file = new File(folder, name + ".sch");
		}
		if(!file.exists()) throw new IllegalArgumentException("Could not find file at path " + file.getPath());
		try {
			schematic = ClipboardFormat.SCHEMATIC.load(file);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Schematic getSchematic() {
		return schematic;
	}
	
	public Config getData() {
		return data;
	}
	
	@Override
	public RoundWorldData create() {
		return data == null ? new SimpleRoundWorldData(worldManager, schematic) : new SimpleRoundWorldData(worldManager, data);
	}
	
}
