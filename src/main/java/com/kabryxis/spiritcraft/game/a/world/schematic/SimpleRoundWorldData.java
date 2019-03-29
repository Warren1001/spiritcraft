package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.FaweCache;
import com.boydti.fawe.example.NMSRelighter;
import com.boydti.fawe.object.FaweQueue;
import com.boydti.fawe.object.schematic.Schematic;
import com.kabryxis.kabutils.data.file.Files;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.kabutils.random.weighted.conditional.ObjectPredicate;
import com.kabryxis.kabutils.spigot.world.Locations;
import com.kabryxis.spiritcraft.game.a.world.Arena;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class SimpleRoundWorldData implements RoundWorldData, ObjectPredicate {
	
	protected final WorldManager worldManager;
	protected final Schematic schematic;
	protected final Config data;
	protected final Arena arena;
	protected final EditSession editSession;
	protected final RandomArrayList<Location> ghostSpawns, hunterSpawns;
	protected final Set<BlockVector2D> occupiedChunks;
	
	protected Region totalRegion;
	
	public SimpleRoundWorldData(WorldManager worldManager, Schematic schematic) {
		this.worldManager = worldManager;
		this.schematic = schematic;
		data = null;
		arena = worldManager.getArenaManager().random(this);
		editSession = arena.getEditSession();
		occupiedChunks = arena.getOccupiedChunks();
		ghostSpawns = new RandomArrayList<>();
		hunterSpawns = new RandomArrayList<>();
	}
	
	public SimpleRoundWorldData(WorldManager worldManager, Config data) {
		this.worldManager = worldManager;
		this.data = data;
		try {
			File dataFile = data.getFile();
			schematic = ClipboardFormat.SCHEMATIC.load(new File(dataFile.getParent(), data.get("schematic", Files.getSimpleName(dataFile)) + ".schematic"));
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		arena = worldManager.getArenaManager().random(this);
		editSession = arena.getEditSession();
		occupiedChunks = arena.getOccupiedChunks();
		Location arenaLoc = arena.getLocation();
		ghostSpawns = new RandomArrayList<>(Integer.MAX_VALUE, data.getList("spawns.ghost",
				o -> arenaLoc.add(Locations.deserialize(o.toString(), arenaLoc.getWorld()))));
		hunterSpawns = new RandomArrayList<>(Integer.MAX_VALUE, data.getList("spawns.hunter",
				o -> arenaLoc.add(Locations.deserialize(o.toString(), arenaLoc.getWorld()))));
	}
	
	@Override
	public Arena getArena() {
		return arena;
	}
	
	@Override
	public void load() {
		Vector loc = arena.getVectorLocation();
		schematic.paste(editSession, loc, false);
		totalRegion = schematic.getClipboard().getRegion();
		try {
			totalRegion.shift(schematic.getClipboard().getOrigin().multiply(-1).add(loc));
		} catch(RegionOperationException e) {
			throw new RuntimeException(e);
		}
		for(int cx = (totalRegion.getMinimumPoint().getBlockX() >> 4) - 1; cx <= (totalRegion.getMaximumPoint().getBlockX() >> 4) + 1; cx++) {
			for(int cz = (totalRegion.getMinimumPoint().getBlockZ() >> 4) - 1; cz <= (totalRegion.getMaximumPoint().getBlockZ() >> 4) + 1; cz++) {
				occupiedChunks.add(new BlockVector2D(cx, cz));
			}
		}
		worldManager.getGame().getTaskManager().start(() -> { // relighting needs to be after all blocks are completely set or some chunks will be improperly lit, idk how better to do this.
			editSession.getQueue().getRelighter().fixSkyLighting();
			worldManager.loadChunks(this, arena.getLocation().getWorld(), occupiedChunks);
			worldManager.getGame().finishSetup();
		}, 60L);
		// lighting bug with corner blocks (or possibly transparent blocks like doors and stairs), light isnt accurately recalculated unless player has the chunk loaded?
		// either design maps with no corner blocks or find out how to relight without lag while players occupy the chunks
		if(data != null) {
			ConfigSection objectivesChild = data.get("objectives");
			if(objectivesChild != null) worldManager.getGame().getObjectiveManager().loadObjectives(objectivesChild);
		}
	}
	
	@Override
	public void relight() {
		worldManager.getGame().getTaskManager().start(() -> ((NMSRelighter)editSession.getQueue().getRelighter()).sendChunks(), 15L); // TODO maybe relight sky again
	}
	
	@Override
	public void unload() {
		//CustomEntityRegistry.removeAll(spawnedEntities); // removes non custom entities as well
		//spawnedEntities.clear();
		editSession.setBlocks(totalRegion, FaweCache.getBlock(0, 0));
		editSession.flushQueue();
		FaweAPI.fixLighting(editSession.getWorld(), totalRegion, editSession.getQueue(), FaweQueue.RelightMode.NONE);
		worldManager.unloadChunks(this);
		worldManager.getGame().getObjectiveManager().clear();
	}
	
	@Override
	public Location getRandomGhostSpawn() {
		return ghostSpawns.random();
	}
	
	@Override
	public Location getRandomHunterSpawn() {
		return hunterSpawns.random();
	}
	
	@Override
	public Location toLocation(Vector vector) {
		return arena.getLocation().add(vector.getX(), vector.getY(), vector.getZ());
	}
	
	@Override
	public Region getTotalRegion() {
		return totalRegion.clone();
	}
	
	@Override
	public boolean test(Object o) {
		return o instanceof Arena && ((Arena)o).fits(schematic.getClipboard().getDimensions());
	}
	
}
