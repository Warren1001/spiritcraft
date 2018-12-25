package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.object.schematic.Schematic;
import com.kabryxis.kabutils.data.file.Files;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.weighted.conditional.ObjectPredicate;
import com.kabryxis.spiritcraft.game.a.world.Arena;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

import java.io.File;
import java.io.IOException;

public class SimpleRoundWorldData implements RoundWorldData, ObjectPredicate {
	
	private final WorldManager worldManager;
	private final Schematic schematic;
	private final Config data;
	private final Arena arena;
	
	public SimpleRoundWorldData(WorldManager worldManager, Schematic schematic) {
		this.worldManager = worldManager;
		this.schematic = schematic;
		this.data = null;
		this.arena = worldManager.getArenaManager().random(this);
	}
	
	public SimpleRoundWorldData(WorldManager worldManager, Config data) {
		this.worldManager = worldManager;
		this.data = data;
		try {
			File dataFile = data.getFile();
			this.schematic = ClipboardFormat.SCHEMATIC.load(new File(dataFile.getParent(), data.get("schematic", Files.getSimpleName(dataFile)) + ".sch"));
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		this.arena = worldManager.getArenaManager().random(this);
	}
	
	@Override
	public Region load() {
		Vector loc = arena.getVectorLocation();
		schematic.paste(arena.getEditSession(), loc, false);
		if(data != null) {
			// TODO load objectives
		}
		Region region = schematic.getClipboard().getRegion();
		return new CuboidRegion(region.getMinimumPoint().add(loc), region.getMaximumPoint().add(loc));
	}
	
	@Override
	public boolean test(Object o) {
		return o instanceof Arena && ((Arena)o).fits(schematic.getClipboard().getDimensions());
	}
	
}
