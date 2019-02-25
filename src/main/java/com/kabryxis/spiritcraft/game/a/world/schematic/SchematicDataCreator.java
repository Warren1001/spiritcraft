package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.kabryxis.kabutils.data.NumberConversions;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SchematicDataCreator { // TODO need to add multischematic support and maybe objectives support?
	
	private final SpiritPlayer player;
	
	public SchematicDataCreator(SpiritPlayer player) {
		this.player = player;
	}
	
	public void reset() {
		name = null;
		weight = 1000;
		ghostSpawns = null;
		hunterSpawns = null;
	}
	
	private String name;
	
	public SchematicDataCreator name(String name) {
		this.name = name;
		return this;
	}
	
	private int weight = 1000;
	
	public SchematicDataCreator weight(int weight) {
		this.weight = weight;
		return this;
	}
	
	private Set<Location> ghostSpawns;
	
	public SchematicDataCreator addGhostSpawn(Location loc) {
		if(ghostSpawns == null) ghostSpawns = new HashSet<>();
		ghostSpawns.add(loc);
		return this;
	}
	
	private Set<Location> hunterSpawns;
	
	public SchematicDataCreator addHunterSpawn(Location loc) {
		if(hunterSpawns == null) hunterSpawns = new HashSet<>();
		hunterSpawns.add(loc);
		return this;
	}
	
	private boolean printOffsetLocation = false;
	
	public SchematicDataCreator printOffsetLocation(boolean printOffsetLocation) {
		this.printOffsetLocation = printOffsetLocation;
		return this;
	}
	
	public boolean printOffsetLocation() {
		return printOffsetLocation;
	}
	
	public void create() {
		if(name == null) {
			player.sendMessage("You did not specify the name for a schematic, how are we suppose to know which schematic's data to modify??");
			return;
		}
		Config data = new Config(new File(player.getGame().getWorldManager().getSchematicManager().getFolder(), name + ".yml"), true);
		if(ghostSpawns != null) data.put("spawns.ghost", ghostSpawns.stream().map(this::serialize).collect(Collectors.toList()));
		if(hunterSpawns != null) data.put("spawns.hunter", hunterSpawns.stream().map(this::serialize).collect(Collectors.toList()));
		data.put("weight", weight);
		data.save();
	}
	
	private String serialize(Location loc) {
		return String.format("%s,%s,%s,%s,%s", NumberConversions.roundToHalf(loc.getX()), NumberConversions.roundToHalf(loc.getY()),
				NumberConversions.roundToHalf(loc.getZ()), NumberConversions.roundToHalf(loc.getYaw()), NumberConversions.roundToHalf(loc.getPitch()));
	}
	
}
