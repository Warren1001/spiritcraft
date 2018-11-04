package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.object.schematic.Schematic;
import com.kabryxis.kabutils.data.file.Files;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SchematicWrapper {
	
	private final String name;
	private final File schematicFile;
	private final Schematic schematic;
	private final Vector origin;
	
	public SchematicWrapper(File schematicFile) {
		this.name = Files.getSimpleName(schematicFile);
		this.schematicFile = schematicFile;
		try {
			this.schematic = ClipboardFormat.SCHEMATIC.load(schematicFile);
			Clipboard clipboard = getClipboard();
			this.origin = clipboard.getOrigin().subtract(clipboard.getMinimumPoint());
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
	
	public Clipboard getClipboard() {
		return Objects.requireNonNull(schematic.getClipboard());
	}
	
	public Vector getOrigin() {
		return origin;
	}
	
}
