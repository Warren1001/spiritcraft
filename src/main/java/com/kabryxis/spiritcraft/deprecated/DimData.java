package com.kabryxis.spiritcraft.deprecated;

import com.boydti.fawe.FaweCache;
import com.boydti.fawe.object.collection.BlockVectorSet;
import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.spiritcraft.game.a.objective.Objective;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.function.pattern.Pattern;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DimData {
	
	private static final BaseBlock AIR = FaweCache.getBlock(0, 0);
	
	private final Map<Block, Objective> objectiveLocations = new HashMap<>();
	private final Set<Vector> modifiedPositions = new BlockVectorSet();
	
	private final ArenaData arenaData;
	private final Schematic schematic;
	private final DimInfo dimInfo;
	private final RandomArrayList<Location> ghostSpawns, hunterSpawns;
	
	public DimData(ArenaData arenaData, Schematic schematic, DimInfo dimInfo) {
		this.arenaData = arenaData;
		this.schematic = schematic;
		this.dimInfo = dimInfo;
		this.ghostSpawns = schematic.getData().getList("spawns.ghost", String.class).stream().map(this::constructLocation).collect(() ->
				new RandomArrayList<>(Integer.MAX_VALUE), RandomArrayList::add, RandomArrayList::addAll);
		this.hunterSpawns = schematic.getData().getList("spawns.hunter", String.class).stream().map(this::constructLocation).collect(() ->
				new RandomArrayList<>(Integer.MAX_VALUE), RandomArrayList::add, RandomArrayList::addAll);
		schematic.getData().getChild("objectives").getChildren().forEach(child -> {
			Block location = constructLocation(child.get("location", String.class)).getBlock();
			//objectiveLocations.put(location, new Objective(arenaData.getGame().getObjectiveManager(), this, location, child));
		});
	}
	
	public Schematic getSchematic() {
		return schematic;
	}
	
	public DimInfo getDimInfo() {
		return dimInfo;
	}
	
	public void addBlockModified(Block block) {
		modifiedPositions.add(new BlockVector(block.getX(), block.getY(), block.getZ()));
	}
	
	public void loadSchematic() {
		EditSession editSession = dimInfo.getEditSession();
		Location start = dimInfo.getLocation();
		int startX = start.getBlockX();
		int startY = start.getBlockY();
		int startZ = start.getBlockZ();
		schematic.getSchematicEntries().forEach(entry -> {
			Vector position = new BlockVector(entry.getX() + startX, entry.getY() + startY, entry.getZ() + startZ);
			try {
				editSession.setBlock(position, FaweCache.getBlock(entry.getType().getId(), entry.getData()));
				modifiedPositions.add(position);
			} catch(MaxChangedBlocksException ignored) {}
		});
		editSession.flushQueue();
	}
	
	public void eraseSchematic() {
		EditSession editSession = dimInfo.getEditSession();
		editSession.setBlocks(modifiedPositions, (Pattern)position -> AIR);
		editSession.flushQueue();
	}
	
	public Location getRandomGhostSpawn() {
		return ghostSpawns.random();
	}
	
	public Location getRandomHunterSpawn() {
		return hunterSpawns.random();
	}
	
	public Location constructLocation(String serializedLoc) {
		Location start = dimInfo.getLocation();
		String[] args = serializedLoc.split(",");
		Location loc = new Location(start.getWorld(), start.getX() + Double.parseDouble(args[0]), start.getY() + Double.parseDouble(args[1]), start.getZ() + Double.parseDouble(args[2]));
		if(args.length == 5) {
			loc.setYaw(Float.parseFloat(args[3]));
			loc.setPitch(Float.parseFloat(args[4]));
		}
		return loc;
	}
	
	public Objective getObjective(Block location) {
		return objectiveLocations.get(location);
	}
	
}
