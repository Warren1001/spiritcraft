package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.weighted.Weighted;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;
import com.sk89q.worldedit.extent.clipboard.Clipboard;

public class ComplexSchematicDataEntry implements Weighted {
	
	private final Clipboard schematic;
	private final Config data;
	private final int weight;
	
	public ComplexSchematicDataEntry(Clipboard schematic) {
		this(schematic, 1000);
	}
	
	public ComplexSchematicDataEntry(Clipboard schematic, int weight) {
		this.schematic = schematic;
		data = null;
		this.weight = weight;
	}
	
	public ComplexSchematicDataEntry(WorldManager worldManager, Config data) {
		schematic = worldManager.getSchematicManager().getSchematic(data.get("schematic", data.getName()));
		this.data = data;
		weight = -1;
	}
	
	public Clipboard getSchematic() {
		return schematic;
	}
	
	public boolean hasData() {
		return data != null;
	}
	
	public Config getData() {
		return data;
	}
	
	@Override
	public int getWeight() {
		return hasData() ? data.getInt("weight", 1000) : weight;
	}
	
}
