package com.kabryxis.spiritcraft.game.a.world;

import com.boydti.fawe.FaweCache;
import com.boydti.fawe.object.collection.BlockVectorSet;
import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.util.EditSessionBuilder;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.kabutils.spigot.world.Locations;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.a.objective.Objective;
import com.kabryxis.spiritcraft.game.a.world.schematic.ArenaSchematic;
import com.kabryxis.spiritcraft.game.a.world.schematic.SchematicWrapper;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;

public class ArenaData {
	
	private static final BaseBlock AIR = FaweCache.getBlock(0, 0);
	
	private final Map<Block, Objective> objectiveLocations = new HashMap<>();
	private final Set<Vector> modifiedPositions = new BlockVectorSet();
	private final Set<SchematicWrapper> otherSchematics = new HashSet<>();
	
	private final Game game;
	private final Arena arena;
	private final ArenaSchematic schematic;
	private final Set<BlockVector2D> occupiedChunks;
	private final EditSession editSession;
	private final RandomArrayList<Location> ghostSpawns, hunterSpawns;
	
	public ArenaData(Game game, Arena arena, ArenaSchematic schematic) {
		this.game = game;
		this.arena = arena;
		this.schematic = schematic;
		this.occupiedChunks = arena.getOccupiedChunks();
		Clipboard clipboard = Objects.requireNonNull(schematic.getSchematic().getClipboard());
		Vector loc = arena.getVectorLocation();
		Vector origin = clipboard.getOrigin();
		for(int cx = ((loc.getBlockX() - origin.getBlockX()) >> 4) - 1; cx <= ((loc.getBlockX() - origin.getBlockX() + clipboard.getDimensions().getBlockX()) >> 4) + 1; cx++) {
			for(int cz = ((loc.getBlockZ() - origin.getBlockZ()) >> 4) - 1; cz <= ((loc.getBlockZ() - origin.getBlockZ() + clipboard.getDimensions().getBlockZ()) >> 4) + 1; cz++) {
				occupiedChunks.add(new BlockVector2D(cx, cz));
			}
		}
		this.editSession = new EditSessionBuilder(arena.getLocation().getWorld().getName()).fastmode(true).autoQueue(true).checkMemory(false)
				.changeSetNull().limitUnlimited().allowedRegionsEverywhere().build();
		this.ghostSpawns = schematic.getData().getList("spawns.ghost", String.class).stream().map(string -> Locations.deserialize(arena.getLocation().getWorld(), string)).collect(() ->
				new RandomArrayList<>(Integer.MAX_VALUE), RandomArrayList::add, RandomArrayList::addAll);
		this.hunterSpawns = schematic.getData().getList("spawns.hunter", String.class).stream().map(string -> Locations.deserialize(arena.getLocation().getWorld(), string)).collect(() ->
				new RandomArrayList<>(Integer.MAX_VALUE), RandomArrayList::add, RandomArrayList::addAll);
		ConfigSection objectivesChild = schematic.getData().getChild("objectives");
		if(objectivesChild != null) {
			objectivesChild.getChildren().forEach(child -> {
				Block location = Locations.deserialize(arena.getLocation().getWorld(), child.get("location", String.class)).getBlock();
				objectiveLocations.put(location, new Objective(this, game.getObjectiveManager(), location, child));
			});
		}
	}
	
	public Game getGame() {
		return game;
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public ArenaSchematic getSchematic() {
		return schematic;
	}
	
	public EditSession getEditSession() {
		return editSession;
	}
	
	public Objective getObjective(Block location) {
		return objectiveLocations.get(location);
	}
	
	public Location getRandomGhostSpawn() {
		return ghostSpawns.random();
	}
	
	public Location getRandomHunterSpawn() {
		return hunterSpawns.random();
	}
	
	public void load() {
		game.getWorldManager().loadChunks(this, arena.getLocation().getWorld(), occupiedChunks);
		paste0(schematic, false);
	}
	
	public void pasteAnotherSchematic(SchematicWrapper schematic, boolean air) {
		otherSchematics.add(schematic);
		paste0(schematic, air);
	}
	
	private void paste0(SchematicWrapper schematicWrapper, boolean air) {
		Schematic schematic = schematicWrapper.getSchematic();
		Clipboard clipboard = Objects.requireNonNull(schematic.getClipboard());
		Set<BlockVector2D> chunkVectors = new HashSet<>();
		Vector loc = arena.getVectorLocation();
		Vector origin = clipboard.getOrigin();
		for(int cx = (loc.getBlockX() - origin.getBlockX()) >> 4; cx <= (loc.getBlockX() - origin.getBlockX() + clipboard.getDimensions().getBlockX()) >> 4; cx++) {
			for(int cz = (loc.getBlockZ() - origin.getBlockZ()) >> 4; cz <= (loc.getBlockZ() - origin.getBlockZ() + clipboard.getDimensions().getBlockZ()) >> 4; cz++) {
				chunkVectors.add(new BlockVector2D(cx, cz));
			}
		}
		schematic.paste(editSession, loc, air);
		editSession.fixLighting(chunkVectors);
	}
	
	public void modifiedPosition(Vector position) {
		modifiedPositions.add(position);
	}
	
	public void modifiedPosition(Block block) {
		modifiedPosition(Vector.toBlockPoint(block.getX(), block.getY(), block.getZ()));
	}
	
	public void unload() {
		try {
			editSession.setBlocks(modifiedPositions, (Pattern)position -> AIR);
			for(SchematicWrapper otherSchematic : otherSchematics) {
				Region otherEraseRegion = Objects.requireNonNull(otherSchematic.getSchematic().getClipboard()).getRegion();
				otherEraseRegion.shift(arena.getVectorLocation());
				editSession.setBlocks(otherEraseRegion, AIR);
			}
			Region eraseRegion = Objects.requireNonNull(schematic.getSchematic().getClipboard()).getRegion();
			eraseRegion.shift(arena.getVectorLocation());
			editSession.setBlocks(eraseRegion, AIR);
			editSession.flushQueue();
			game.getWorldManager().unloadChunks(this);
		} catch(RegionOperationException e) {
			throw new RuntimeException(e);
		}
	}
	
}
