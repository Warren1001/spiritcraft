package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;

import java.io.File;
import java.io.IOException;

public class SchematicWrapper {
	
	private final String name;
	private final File schematicFile;
	private final Schematic schematic;
	
	public SchematicWrapper(File schematicFile) {
		this.name = schematicFile.getName().split("\\.", 2)[0];
		this.schematicFile = schematicFile;
		try {
			this.schematic = ClipboardFormat.SCHEMATIC.load(schematicFile);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Schematic getSchematic() {
		return schematic;
	}
	
}
