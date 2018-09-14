package com.kabryxis.spiritcraft.deprecated;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.weighted.Weighted;
import com.kabryxis.kabutils.spigot.world.schematic.SchematicEntry;

import java.io.File;
import java.util.Set;

public class Schematic implements Weighted {
	
	private final String name;
	private final File file;
	private final Set<SchematicEntry> schematicEntries;
	private final Config data;
	
	public Schematic(String name, File file, Set<SchematicEntry> schematicEntries, Config data) {
		this.name = name;
		this.file = file;
		this.schematicEntries = schematicEntries;
		this.data = data;
		data.loadSync();
	}
	
	public String getName() {
		return name;
	}
	
	public Config getData() {
		return data;
	}
	
	public Set<SchematicEntry> getSchematicEntries() {
		return schematicEntries;
	}
	
	@Override
	public int getWeight() {
		return data.get("weight", Integer.class, 100);
	}
	
}
