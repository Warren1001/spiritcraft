package com.kabryxis.spiritcraft.game;

import com.kabryxis.spiritcraft.game.a.world.SchematicManager;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.data.MathHelp;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.spigot.world.schematic.BlockSelection;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SchematicCreator {
	
	private final SpiritPlayer player;
	
	public SchematicCreator(SpiritPlayer player) {
		this.player = player;
	}
	
	public void reset() {
		name = null;
		weight = 1000;
		ghostSpawns = null;
		hunterSpawns = null;
	}
	
	private String name;
	
	public SchematicCreator name(String name) {
		this.name = name;
		return this;
	}
	
	private int weight = 1000;
	
	public SchematicCreator weight(int weight) {
		this.weight = weight;
		return this;
	}
	
	private Set<Location> ghostSpawns;
	
	public SchematicCreator addGhostSpawn(Location loc) {
		if(ghostSpawns == null) ghostSpawns = new HashSet<>();
		ghostSpawns.add(loc);
		return this;
	}
	
	private Set<Location> hunterSpawns;
	
	public SchematicCreator addHunterSpawn(Location loc) {
		if(hunterSpawns == null) hunterSpawns = new HashSet<>();
		hunterSpawns.add(loc);
		return this;
	}
	
	public void create() {
		Validate.isTrue(name != null, "A name was not specified!"); // TODO replace with user messaging, this is current temporary self-dummyproof
		Validate.isTrue(ghostSpawns != null, "No ghost spawns were specified!");
		Validate.isTrue(hunterSpawns != null, "No hunter spawns were specified!");
		SchematicManager manager = player.getGame().getWorldManager().getSchematicManager();
		BlockSelection selection = player.getSelection();
		Config data = new Config(new File(manager.getFolder(), name + "-data.yml"));
		List<String> serializedGhostSpawns = new ArrayList<>(ghostSpawns.size());
		ghostSpawns.forEach(loc -> serializedGhostSpawns.add(serialize(loc)));
		data.set("spawns.ghost", serializedGhostSpawns);
		List<String> serializedHunterSpawns = new ArrayList<>(hunterSpawns.size());
		hunterSpawns.forEach(loc -> serializedHunterSpawns.add(serialize(loc)));
		data.set("spawns.hunter", serializedHunterSpawns);
		data.set("weight", weight);
		data.save();
		manager.create(name, selection, data);
	}
	
	private String serialize(Location loc) {
		BlockSelection selection = player.getSelection();
		return MathHelp.roundToHalf(loc.getX() - selection.getLowestX()) + "," + MathHelp.roundToHalf(loc.getY() - selection.getLowestY()) + "," +
				MathHelp.roundToHalf(loc.getZ() - selection.getLowestZ()) + "," + loc.getYaw() + "," + loc.getPitch();
	}
	
}
