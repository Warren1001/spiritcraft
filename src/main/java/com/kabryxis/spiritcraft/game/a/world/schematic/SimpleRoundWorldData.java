package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.collection.LocalBlockVector2DSet;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.kabryxis.kabutils.data.file.KFiles;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.kabutils.random.weighted.conditional.ObjectPredicate;
import com.kabryxis.kabutils.spigot.world.Locations;
import com.kabryxis.spiritcraft.game.a.world.Arena;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class SimpleRoundWorldData implements RoundWorldData, ObjectPredicate {
	
	protected final WorldManager worldManager;
	protected final Clipboard schematic;
	protected final Config data;
	protected final Arena arena;
	protected final boolean hasWeather;
	protected final boolean hasLightning;
	protected final EditSession editSession;
	protected final RandomArrayList<Location> ghostSpawns, hunterSpawns;
	protected final LocalBlockVector2DSet occupiedChunks;
	
	protected Region totalRegion;
	
	public SimpleRoundWorldData(WorldManager worldManager, Clipboard schematic) {
		this.worldManager = worldManager;
		this.schematic = schematic;
		data = null;
		hasWeather = false;
		hasLightning = false;
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
			schematic = FaweAPI.load(new File(dataFile.getParent(), data.get("schematic", KFiles.getSimpleName(dataFile)) + ".schematic"));
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		arena = worldManager.getArenaManager().random(this);
		hasWeather = data.getBoolean("weather", false);
		hasLightning = data.getBoolean("lightning", false);
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
		System.out.println("SimpleRoundWorldData loaded");
		BlockVector3 loc = arena.getVectorLocation();
		schematic.paste(editSession, loc, false);
		totalRegion = schematic.getRegion();
		try {
			BlockVector3 origin = schematic.getOrigin().multiply(-1);
			totalRegion.shift(loc.add(origin.getX(), origin.getY(), origin.getZ()));
		} catch(RegionOperationException e) {
			throw new RuntimeException(e);
		}
		for(int cx = (totalRegion.getMinimumPoint().getBlockX() >> 4) - 1; cx <= (totalRegion.getMaximumPoint().getBlockX() >> 4) + 1; cx++) {
			for(int cz = (totalRegion.getMinimumPoint().getBlockZ() >> 4) - 1; cz <= (totalRegion.getMaximumPoint().getBlockZ() >> 4) + 1; cz++) {
				occupiedChunks.add(cx, cz);
			}
		}
		worldManager.getGame().getTaskManager().start(() -> { // relighting needs to be after all blocks are completely set or some chunks will be improperly lit, idk how better to do this.
			//editSession.getQueue().getRelighter().fixSkyLighting();
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
		//worldManager.getGame().getTaskManager().start(() -> ((NMSRelighter)editSession.getQueue().getRelighter()).sendChunks(), 15L); // TODO maybe relight sky again
		if(hasWeather) {
			PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
			packet.getIntegers().write(0, 7);
			packet.getFloat().write(0, 1F);
			worldManager.getGame().forEachPlayer(player -> {
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(player.getPlayer(), packet);
				} catch(InvocationTargetException e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	@Override
	public void unload() {
		//CustomEntityRegistry.removeAll(spawnedEntities); // removes non custom entities as well
		//spawnedEntities.clear();
		editSession.setBlocks(totalRegion, BlockTypes.AIR);
		editSession.flushQueue();
		//FaweAPI.fixLighting(editSession.getWorld(), totalRegion);
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
	public Location toLocation(BlockVector3 vector) {
		return arena.getLocation().add(vector.getX(), vector.getY(), vector.getZ());
	}
	
	@Override
	public Region getTotalRegion() {
		return totalRegion.clone();
	}
	
	@Override
	public boolean test(Object o) {
		return o instanceof Arena && ((Arena)o).fits(schematic.getDimensions());
	}
	
}
