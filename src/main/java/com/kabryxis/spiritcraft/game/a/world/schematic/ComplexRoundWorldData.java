package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.object.collection.LocalBlockVector2DSet;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.kabutils.spigot.world.Locations;
import com.kabryxis.spiritcraft.game.a.world.Arena;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BaseBlock;
import org.bukkit.Location;
import org.bukkit.block.Biome;

import java.util.List;
import java.util.stream.Collectors;

public class ComplexRoundWorldData implements RoundWorldData {
	
	protected final WorldManager worldManager;
	protected final Config data;
	protected final Arena arena;
	protected final BiomeType biome;
	protected final boolean hasWeather;
	protected final boolean hasLightning;
	protected final List<ComplexSchematicDataEntry> dataEntries;
	protected final EditSession editSession;
	protected final RandomArrayList<Location> ghostSpawns, hunterSpawns;
	protected final LocalBlockVector2DSet occupiedChunks;
	
	protected Region totalRegion;
	
	public ComplexRoundWorldData(WorldManager worldManager, Config data, List<RandomArrayList<ComplexSchematicDataEntry>> dataContainerLists) {
		this.worldManager = worldManager;
		this.data = data;
		List<ComplexSchematicDataEntry> dataEntries = null;
		Arena arena = null;
		int count = 0;
		while(arena == null && count < 10) { // TODO there is definitely a better way to do this
			List<ComplexSchematicDataEntry> dataEntriesCopy = dataEntries = dataContainerLists.stream()
					.map(RandomArrayList::random).collect(Collectors.toList());
			arena = worldManager.getArenaManager().random(object -> object instanceof Arena &&
					dataEntriesCopy.stream().allMatch(dataEntry -> ((Arena)object).fits(dataEntry.getSchematic().getDimensions())));
			count++;
		}
		if(arena == null) throw new IllegalStateException("no arena compatible with a certain ComplexRoundWorldData");
		this.arena = arena;
		Biome bukkitBiome = data.getEnum("biome", Biome.class);
		biome = bukkitBiome == null ? null : BiomeTypes.getLegacy(bukkitBiome.ordinal());
		hasWeather = data.getBoolean("weather", false);
		hasLightning = data.getBoolean("lightning", false);
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
		System.out.println("ComplexRoundWorldData loaded");
		Location arenaLoc = arena.getLocation();
		for(ComplexSchematicDataEntry dataEntry : dataEntries) {
			Clipboard schematic = dataEntry.getSchematic();
			schematic.paste(editSession, arena.getVectorLocation(), false);
			recalcTotalRegion(schematic);
			if(dataEntry.hasData()) {
				Config data = dataEntry.getData();
				if(data.containsKey("spawns.ghost")) ghostSpawns.addAll(data.getList("spawns.ghost", o -> arenaLoc.add(Locations.deserialize(o.toString(), arenaLoc.getWorld()))));
				if(data.containsKey("spawns.hunter")) hunterSpawns.addAll(data.getList("spawns.hunter", o -> arenaLoc.add(Locations.deserialize(o.toString(), arenaLoc.getWorld()))));
				ConfigSection objectivesChild = data.get("objectives");
				if(objectivesChild != null) worldManager.getGame().getObjectiveManager().loadObjectives(objectivesChild);
			}
		}
		try {
			totalRegion.expand(BlockVector3.at(-32, 0, -32), BlockVector3.at(32, 0, 32));
		} catch(RegionOperationException e) {
			e.printStackTrace();
		}
		for(int cx = (totalRegion.getMinimumPoint().getBlockX() >> 4); cx <= (totalRegion.getMaximumPoint().getBlockX() >> 4); cx++) {
			for(int cz = (totalRegion.getMinimumPoint().getBlockZ() >> 4); cz <= (totalRegion.getMaximumPoint().getBlockZ() >> 4); cz++) {
				occupiedChunks.add(cx, cz);
			}
		}
		BlockVector3 min = totalRegion.getMinimumPoint(), max = totalRegion.getMaximumPoint();
		for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
			for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
				editSession.setBiome(x, 0, z, biome);
			}
		}
		editSession.flushQueue();
		//worldManager.getGame().getTaskManager().start(() -> { // relighting needs to be after all blocks are completely set or some chunks will be improperly lit, idk how better to do this.
		//editSession.getBlockBag().getQueue().getRelighter().removeAndRelight(true); TODO
		//worldManager.loadChunks(this, arena.getLocation().getWorld(), occupiedChunks);
		//editSession.getQueue().getRelighter().fixSkyLighting();
			//FaweAPI.fixLighting(editSession.getWorld(), totalRegion, editSession.getQueue(), FaweQueue.RelightMode.ALL);
		worldManager.getGame().finishSetup();
		//}, 60L);
		// lighting bug with corner blocks (or possibly transparent blocks like doors and stairs), light isnt accurately recalculated unless player has the chunk loaded?
		// either design maps with no corner blocks or find out how to relight without lag while players occupy the chunks
	}
	
	private void recalcTotalRegion(Clipboard newSchematic) {
		BlockVector3 loc = arena.getVectorLocation();
		Region region = newSchematic.getRegion();
		try {
			BlockVector3 origin = newSchematic.getOrigin();
			region.shift(loc.subtract(origin.getX(), origin.getY(), origin.getZ()));
		} catch(RegionOperationException e) {
			throw new RuntimeException(e);
		}
		if(totalRegion == null) {
			totalRegion = region;
			return;
		}
		double mix, miy, miz, max, may, maz;
		BlockVector3 currMin = totalRegion.getMinimumPoint(), currMax = totalRegion.getMaximumPoint(),
				newMin = region.getMinimumPoint(), newMax = region.getMaximumPoint();
		mix = Math.min(currMin.getX(), newMin.getX());
		miy = Math.min(currMin.getY(), newMin.getY());
		miz = Math.min(currMin.getZ(), newMin.getZ());
		max = Math.max(currMax.getX(), newMax.getX());
		may = Math.max(currMax.getY(), newMax.getY());
		maz = Math.max(currMax.getZ(), newMax.getZ());
		totalRegion = new CuboidRegion(BlockVector3.at(mix, miy, miz), BlockVector3.at(max, may, maz));
	}
	
	@Override
	public void relight() {
		//worldManager.getGame().getTaskManager().start(() -> occupiedChunks.forEach(cv -> arena.getLocation().getWorld().refreshChunk(cv.getBlockX(), cv.getBlockZ())), 20L);
		//worldManager.getGame().getTaskManager().start(() -> FaweAPI.fixLighting(editSession.getWorld(), totalRegion, editSession.getQueue(), FaweQueue.RelightMode.OPTIMAL), 20L);
		//worldManager.getGame().getTaskManager().start(() -> ((NMSRelighter)editSession.getQueue().getRelighter()).sendChunks(), 15L); // TODO maybe relight sky again
		if(hasWeather) {
			PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
			packet.getIntegers().write(0, 7);
			packet.getFloat().write(0, 1F);
			worldManager.getGame().forEachPlayer(player -> player.getProtocolLibAdapter().sendPacket(packet));
		}
	}
	
	@Override
	public void unload() {
		//CustomEntityRegistry.removeAll(spawnedEntities); // removes non custom entities as well
		//spawnedEntities.clear();
		editSession.setBlocks(totalRegion, BaseBlock.getState(0, 0));
		editSession.flushQueue();
		//FaweAPI.fixLighting(editSession.getWorld(), totalRegion, editSession.getQueue(), FaweQueue.RelightMode.ALL);
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
	public Location toLocation(BlockVector3 pos) {
		return arena.getLocation().add(pos.getX(), pos.getY(), pos.getZ());
	}
	
	@Override
	public Region getTotalRegion() {
		return totalRegion;
	}
	
}
