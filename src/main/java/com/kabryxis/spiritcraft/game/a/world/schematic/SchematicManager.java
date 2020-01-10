package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.extent.clipboard.Clipboard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SchematicManager {

	private final Map<String, Clipboard> schematics = new HashMap<>();
	
	private final File folder;
	
	public SchematicManager(File folder) {
		this.folder = folder;
		folder.mkdirs();
	}
	
	public File getFolder() {
		return folder;
	}
	
	public Clipboard getSchematic(String name) {
		return schematics.computeIfAbsent(name, n -> {
			try {
				return FaweAPI.load(new File(folder, n.endsWith(".schematic") ? n : n + ".schematic"));
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

}
