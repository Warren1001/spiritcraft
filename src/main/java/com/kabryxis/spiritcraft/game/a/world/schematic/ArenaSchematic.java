package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.weighted.Weighted;

import java.io.File;

public class ArenaSchematic extends SchematicWrapper implements Weighted {
	
	private final Config data;
	
	public ArenaSchematic(File schematicFile) {
		this(schematicFile, new File(schematicFile.getParentFile(), schematicFile.getName().split("\\.", 2)[0] + "-data.yml"));
	}
	
	public ArenaSchematic(File schematicFile, File dataFile) {
		this(schematicFile, new Config(dataFile));
		data.loadSync();
	}
	
	public ArenaSchematic(File schematicFile, Config schematicData) {
		super(schematicFile);
		this.data = schematicData;
	}
	
	public Config getData() {
		return data;
	}
	
	@Override
	public int getWeight() {
		return data.get("weight", Integer.class, 1000);
	}
	
}
