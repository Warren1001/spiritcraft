package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.FaweCache;
import com.boydti.fawe.example.NMSRelighter;
import com.boydti.fawe.object.FaweQueue;
import com.boydti.fawe.object.schematic.Schematic;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.kabutils.spigot.world.Locations;
import com.kabryxis.spiritcraft.game.a.world.Arena;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import org.bukkit.Location;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ComplexRoundWorldData implements RoundWorldData {
	
	protected final WorldManager worldManager;
	protected final Arena arena;
	protected final List<ComplexSchematicDataEntry> dataEntries;
	protected final EditSession editSession;
	protected final RandomArrayList<Location> ghostSpawns, hunterSpawns;
	protected final Set<Vector2D> occupiedChunks;
	
	protected Region totalRegion;
	
	public ComplexRoundWorldData(WorldManager worldManager, List<RandomArrayList<ComplexSchematicDataEntry>> dataContainerLists) {
		this.worldManager = worldManager;
		List<ComplexSchematicDataEntry> dataEntries = null;
		Arena arena = null;
		int count = 0;
		while(arena == null && count < 10) { // TODO there is definitely a better way to do this
			List<ComplexSchematicDataEntry> dataEntriesCopy = dataEntries = dataContainerLists.stream()
					.map(RandomArrayList::random).collect(Collectors.toList());
			arena = worldManager.getArenaManager().random(object -> object instanceof Arena &&
					dataEntriesCopy.stream().allMatch(dataEntry -> ((Arena)object).fits(dataEntry.getSchematic().getClipboard().getDimensions())));
			count++;
		}
		if(arena == null) throw new IllegalStateException("no arena compatible with a certain ComplexRoundWorldData");
		this.arena = arena;
		this.dataEntries = dataEntries;
		editSession = arena.getEditSession();
		occupiedChunks = arena.getOccupiedChunks();
		Location arenaLoc = arena.getLocation();
		ghostSpawns = new RandomArrayList<>(Integer.MAX_VALUE);
		hunterSpawns = new RandomArrayList<>(Integer.MAX_VALUE);
	}
	
	@Override
	public Arena getArena() {
		return arena;
	}
	
	@Override
	public void load() {
		Location arenaLoc = arena.getLocation();
		for(ComplexSchematicDataEntry dataEntry : dataEntries) {
			Schematic schematic = dataEntry.getSchematic();
			schematic.paste(editSession, arena.getVectorLocation(), false);
			recalcTotalRegion(schematic);
			for(int cx = (totalRegion.getMinimumPoint().getBlockX() >> 4) - 1; cx <= (totalRegion.getMaximumPoint().getBlockX() >> 4) + 1; cx++) {
				for(int cz = (totalRegion.getMinimumPoint().getBlockZ() >> 4) - 1; cz <= (totalRegion.getMaximumPoint().getBlockZ() >> 4) + 1; cz++) {
					occupiedChunks.add(new BlockVector2D(cx, cz));
				}
			}
			if(dataEntry.hasData()) {
				Config data = dataEntry.getData();
				if(data.containsKey("spawns.ghost")) ghostSpawns.addAll(data.getList("spawns.ghost", o -> arenaLoc.add(Locations.deserialize(o.toString(), arenaLoc.getWorld()))));
				if(data.containsKey("spawns.hunter")) hunterSpawns.addAll(data.getList("spawns.hunter", o -> arenaLoc.add(Locations.deserialize(o.toString(), arenaLoc.getWorld()))));
				ConfigSection objectivesChild = data.get("objectives");
				if(objectivesChild != null) worldManager.getGame().getObjectiveManager().loadObjectives(objectivesChild);
			}
		}
		worldManager.getGame().getTaskManager().start(() -> { // relighting needs to be after all blocks are completely set or some chunks will be improperly lit, idk how better to do this.
			editSession.getQueue().getRelighter().fixSkyLighting();
			worldManager.loadChunks(this, arena.getLocation().getWorld(), occupiedChunks);
			worldManager.getGame().finishSetup();
		}, 60L);
		// lighting bug with corner blocks (or possibly transparent blocks like doors and stairs), light isnt accurately recalculated unless player has the chunk loaded?
		// either design maps with no corner blocks or find out how to relight without lag while players occupy the chunks
	}
	
	private void recalcTotalRegion(Schematic newSchematic) {
		Vector loc = arena.getVectorLocation();
		Region region = newSchematic.getClipboard().getRegion();
		try {
			region.shift(loc.subtract(newSchematic.getClipboard().getOrigin()));
		} catch(RegionOperationException e) {
			throw new RuntimeException(e);
		}
		if(totalRegion == null) {
			totalRegion = region;
			return;
		}
		double mix, miy, miz, max, may, maz;
		Vector currMin = totalRegion.getMinimumPoint(), currMax = totalRegion.getMaximumPoint(),
				newMin = region.getMinimumPoint(), newMax = region.getMaximumPoint();
		mix = Math.min(currMin.getX(), newMin.getX());
		miy = Math.min(currMin.getY(), newMin.getY());
		miz = Math.min(currMin.getZ(), newMin.getZ());
		max = Math.max(currMax.getX(), newMax.getX());
		may = Math.max(currMax.getY(), newMax.getY());
		maz = Math.max(currMax.getZ(), newMax.getZ());
		totalRegion = new CuboidRegion(new Vector(mix, miy, miz), new Vector(max, may, maz));
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
	public Location toLocation(Vector pos) {
		return arena.getLocation().add(pos.getX(), pos.getY(), pos.getZ());
	}
	
	@Override
	public Region getTotalRegion() {
		return totalRegion;
	}
	
}
