package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.weighted.Weighted;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;

import java.io.File;

public class ArenaSchematic extends SchematicWrapper implements Weighted {
	
	private final Config data;
	
	public ArenaSchematic(Config data) {
		super(new File(data.getFile().getParent(), String.format("%s.%s", data.get("sch", data.getName()), ClipboardFormat.SCHEMATIC.getExtension())));
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
