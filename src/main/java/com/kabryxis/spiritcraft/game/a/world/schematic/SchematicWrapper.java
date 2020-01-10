package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.FaweAPI;
import com.kabryxis.kabutils.data.file.KFiles;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;

import java.io.File;
import java.io.IOException;

public class SchematicWrapper {
	
	private final String name;
	private final File schematicFile;
	private final Clipboard schematic;
	private final BlockVector3 origin;
	
	public SchematicWrapper(File schematicFile) {
		this.name = KFiles.getSimpleName(schematicFile);
		this.schematicFile = schematicFile;
		try {
			this.schematic = FaweAPI.load(schematicFile);
			BlockVector3 min = schematic.getMinimumPoint();
			this.origin = schematic.getOrigin().subtract(min.getBlockX(), min.getBlockY(), min.getBlockZ());
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Clipboard getSchematic() {
		return schematic;
	}
	
	public BlockVector3 getOrigin() {
		return origin;
	}
	
}
