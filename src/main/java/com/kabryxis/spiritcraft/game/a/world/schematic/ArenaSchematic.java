package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.weighted.Weighted;

import java.io.File;

public class ArenaSchematic extends SchematicWrapper implements Weighted {
	
	private final Config data;
	
	public ArenaSchematic(Config data) {
		super(new File(data.getFile().getParent(), String.format("%s.sch", data.get("sch", data.getName()))));
		this.data = data;
	}
	
	public ArenaSchematic(File schematicFile, File dataFile) {
		this(schematicFile, new Config(dataFile, true));
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
		return data.getInt("weight", 1000);
	}
	
}
