package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.object.schematic.Schematic;
import com.kabryxis.kabutils.data.MathHelp;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SchematicDataCreator {
	
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
	private Vector offset;
	
	public SchematicDataCreator name(String name) {
		this.name = name;
		SchematicWrapper schematicWrapper = player.getGame().getWorldManager().getSchematicManager().getSchematic(name);
		Schematic schematic = null;
		if(schematicWrapper == null) {
			try {
				schematic = ClipboardFormat.SCHEMATIC.load(new File("plugins" + File.separator + "WorldEdit" +
						File.separator + "schematics" + File.separator + player.getUniqueId(), name + ".schematic"));
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		else schematic = schematicWrapper.getSchematic();
		if(schematic == null) {
			player.sendMessage("Could not find a schematic named '" + name + "'.");
			return this;
		}
		offset = Objects.requireNonNull(schematic.getClipboard()).getOrigin();
		return this;
	}
	
	public SchematicDataCreator offset(Location location) {
		this.offset = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
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
	
	public Vector getOffsetLocation(Location location) {
		if(offset == null) {
			player.sendMessage("You must specify a name to get the corresponding schematic or manually specify an offset before you can get offseted locations.");
			return null;
		}
		return new Vector(location.getX() - offset.getX(), location.getY() - offset.getY(), location.getZ() - offset.getZ());
	}
	
	public void create() {
		if(name == null) {
			player.sendMessage("You did not specify the name for a schematic, how are we suppose to know which schematic's data to modify??");
			return;
		}
		Config data = new Config(new File(player.getGame().getWorldManager().getSchematicManager().getFolder(), name + "-data.yml"));
		if(ghostSpawns != null) data.set("spawns.ghost", ghostSpawns.stream().map(loc -> serialize(loc, offset)).collect(Collectors.toList()));
		if(hunterSpawns != null) data.set("spawns.hunter", hunterSpawns.stream().map(loc -> serialize(loc, offset)).collect(Collectors.toList()));
		data.set("weight", weight);
		data.save();
	}
	
	private String serialize(Location loc, Vector offset) {
		Region selection = player.getSelection();
		return MathHelp.roundToHalf(loc.getX() - offset.getBlockX()) + "," + MathHelp.roundToHalf(loc.getY() - offset.getBlockY()) + "," +
				MathHelp.roundToHalf(loc.getZ() - offset.getBlockZ()) + "," + loc.getYaw() + "," + loc.getPitch();
	}
	
}
