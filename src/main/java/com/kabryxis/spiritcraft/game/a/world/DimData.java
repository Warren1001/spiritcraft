package com.kabryxis.spiritcraft.game.a.world;

import com.boydti.fawe.FaweCache;
import com.boydti.fawe.object.collection.BlockVectorSet;
import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.spiritcraft.game.Schematic;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.function.pattern.Pattern;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;

public class DimData {
	
	private static final BaseBlock AIR = FaweCache.getBlock(0, 0);
	
	private final Set<Vector> modifiedPositions = new BlockVectorSet();
	private final Map<Block, PowerBlock> powerBlocks = new HashMap<>();
	
	private final ArenaData arenaData;
	private final Schematic schematic;
	private final DimInfo dimInfo;
	private final RandomArrayList<Location> ghostSpawns, hunterSpawns;
	//private final RandomArrayList<Location> itemDropLocs;
	
	public DimData(ArenaData arenaData, Schematic schematic, DimInfo dimInfo) {
		this.arenaData = arenaData;
		this.schematic = schematic;
		this.dimInfo = dimInfo;
		List<String> ghostSpawnsStrings = schematic.getData().getList("spawns.ghost", String.class);
		this.ghostSpawns = new RandomArrayList<>(ghostSpawnsStrings.size(), ghostSpawnsStrings.size());
		constructLocations(ghostSpawns, ghostSpawnsStrings);
		List<String> hunterSpawnStrings = schematic.getData().getList("spawns.hunter", String.class);
		this.hunterSpawns = new RandomArrayList<>(hunterSpawnStrings.size(), hunterSpawnStrings.size());
		constructLocations(hunterSpawns, hunterSpawnStrings);
		/*this.itemDropLocs = new RandomArrayList<>(2);
		constructLocations(itemDropLocs, schematic.getData().getList("locations.item-drops", String.class));
		Set<Location> blocksToPowerLocs = new HashSet<>();
		constructLocations(blocksToPowerLocs, schematic.getData().getList("locations.power-blocks", String.class));
		blocksToPowerLocs.forEach(loc -> blocksToPower.add(loc.getBlock())); TODO */
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
		editSession.setBlocks(modifiedPositions, (Pattern)position -> ArenaManager.AIR);
		editSession.flushQueue();
	}
	
	public void registerPowerBlock(PowerBlock powerBlock) {
		powerBlocks.put(powerBlock.getBlock(), powerBlock);
	}
	
	public boolean isPowerBlock(Block block) {
		return powerBlocks.containsKey(block);
	}
	
	public PowerBlock getPowerBlock(Block block) {
		return powerBlocks.get(block);
	}
	
	public Location getRandomGhostSpawn() {
		return ghostSpawns.random();
	}
	
	public Location getRandomHunterSpawn() {
		return hunterSpawns.random();
	}
	
	private void constructLocations(Collection<Location> locs, List<String> list) {
		Location start = dimInfo.getLocation();
		for(String string : list) {
			String[] args = string.split(",");
			locs.add(new Location(start.getWorld(), start.getX() + Double.parseDouble(args[0]), start.getY() + Double.parseDouble(args[1]),
					start.getZ() + Double.parseDouble(args[2]), Float.parseFloat(args[3]), Float.parseFloat(args[4])));
		}
	}
	
}
