package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SchematicManager {

	private final Map<String, Schematic> schematics = new HashMap<>();
	
	private final File folder;
	
	public SchematicManager(File folder) {
		this.folder = folder;
		folder.mkdirs();
	}
	
	public File getFolder() {
		return folder;
	}
	
	public Schematic getSchematic(String name) {
		return schematics.computeIfAbsent(name, n -> {
			try {
				return ClipboardFormat.SCHEMATIC.load(new File(folder, n.endsWith(".schematic") ? n : n + ".schematic"));
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

}
