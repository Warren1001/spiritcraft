package com.kabryxis.spiritcraft.game.a.world;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.FaweCache;
import com.boydti.fawe.example.NMSRelighter;
import com.boydti.fawe.object.FaweQueue;
import com.boydti.fawe.object.schematic.Schematic;
import com.kabryxis.kabutils.data.Lists;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.kabutils.spigot.version.custom.CustomEntityRegistry;
import com.kabryxis.kabutils.spigot.world.BlockStateManager;
import com.kabryxis.kabutils.spigot.world.Locations;
import com.kabryxis.spiritcraft.Spiritcraft;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.a.world.schematic.ArenaSchematic;
import com.kabryxis.spiritcraft.game.a.world.schematic.SchematicWrapper;
import com.kabryxis.spiritcraft.game.object.type.GameObjectBase;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.material.Door;

import java.util.*;

public class ArenaData {
	
	public static final BaseBlock AIR = FaweCache.getBlock(0, 0);
	
	protected final SpiritGame game;
	protected final Arena arena;
	protected final Location location;
	protected final Vector vectorLocation;
	protected final ArenaSchematic schematic;
	protected final EditSession editSession;
	protected final RandomArrayList<Location> ghostSpawns, hunterSpawns;
	protected final Set<Material> protectedBlocks = EnumSet.noneOf(Material.class);
	protected final Set<Entity> spawnedEntities = new HashSet<>();
	
	protected Region totalRegion;
	protected Set<Vector2D> occupiedChunks;
	protected boolean afterSetup = false;
	
	public ArenaData(SpiritGame game, Arena arena, ArenaSchematic schematic) {
		this.game = game;
		this.arena = arena;
		location = arena.getLocation().clone();
		Clipboard clipboard = schematic.getClipboard();
		if(arena.isDynamic()) location.setY(Math.abs(schematic.getOrigin().getBlockY()) + 1);
		vectorLocation = new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		World world = location.getWorld();
		occupiedChunks = arena.getOccupiedChunks();
		this.schematic = schematic;
		editSession = game.getWorldManager().getEditSession(world);
		ghostSpawns = new RandomArrayList<>(Integer.MAX_VALUE, Lists.convert(schematic.getData().get("spawns.ghost"), o -> getArenaLocation(Locations.deserialize(o.toString(), world))));
		hunterSpawns = new RandomArrayList<>(Integer.MAX_VALUE, Lists.convert(schematic.getData().get("spawns.hunter"), o -> getArenaLocation(Locations.deserialize(o.toString(), world))));
		Region region = getModifyingRegion(clipboard);
		for(int cx = (region.getMinimumPoint().getBlockX() >> 4) - 1; cx <= (region.getMaximumPoint().getBlockX() >> 4) + 1; cx++) {
			for(int cz = (region.getMinimumPoint().getBlockZ() >> 4) - 1; cz <= (region.getMaximumPoint().getBlockZ() >> 4) + 1; cz++) {
				occupiedChunks.add(new BlockVector2D(cx, cz));
			}
		}
	}
	
	public SpiritGame getGame() {
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
	
	public GameObjectBase getObjective(Block location) {
		return game.getObjectiveManager().getObjective(location);
	}
	
	public Location getRandomGhostSpawn() {
		return ghostSpawns.random();
	}
	
	public Location getRandomHunterSpawn() {
		return hunterSpawns.random();
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Vector getVectorLocation() {
		return vectorLocation;
	}

	public BlockStateManager getBlockStateManager(World world) {
		return game.getWorldManager().getBlockStateManager(world);
	}

	public Location getArenaLocation(Location loc) {
		return loc.add(location);
	}
	
	public Location toLocation(String serializedOffset) {
		return toLocation(Spiritcraft.DESERIALIZE_VECTOR.apply(serializedOffset));
	}
	
	public Location toLocation(Vector offset) {
		return location.clone().add(offset.getX(), offset.getY(), offset.getZ());
	}
	
	public Vector toOffset(Location loc) {
		return new Vector(loc.getX() - location.getX(), loc.getY() - location.getY(), loc.getZ() - location.getZ());
	}
	
	public Region getRegion() {
		return totalRegion;
	}
	
	public void load() {
		ConfigSection objectivesChild = schematic.getData().get("objectives");
		if(objectivesChild != null) game.getObjectiveManager().loadObjectives(objectivesChild);
		// game.getWorldManager().loadChunks(this, location.getWorld(), occupiedChunks);
		paste0(schematic, true);
		game.getTaskManager().start(() -> { // relighting needs to be after all blocks are completely set or some chunks will be improperly lit, idk how better to do this.
			editSession.getQueue().getRelighter().fixSkyLighting();
			game.getWorldManager().loadChunks(this, location.getWorld(), occupiedChunks);
			afterSetup = true;
			game.finishSetup();
		}, 60L);
		// lighting bug with corner blocks (or possibly transparent blocks like doors and stairs), light isnt accurately recalculated unless player has the chunk loaded
		// either design maps with no corner blocks or find out how to relight without lag while players occupy the chunks
	}
	
	public void pasteAnotherSchematic(SchematicWrapper schematic, boolean air) {
		paste0(schematic, air);
	}
	
	private void paste0(SchematicWrapper schematicWrapper, boolean air) {
		if(schematicWrapper instanceof ArenaSchematic) protectedBlocks.addAll(((ArenaSchematic)schematicWrapper).getProtectedBlocks());
		Schematic schematic = schematicWrapper.getSchematic();
		Region region = getModifyingRegion(schematicWrapper.getClipboard());
		Vector min = region.getMinimumPoint();
		Vector max = region.getMaximumPoint();
		if(totalRegion == null) totalRegion = region;
		else {
			boolean createNewRegion = false;
			if(!totalRegion.contains(min)) {
				createNewRegion = true;
				Vector curr = totalRegion.getMinimumPoint();
				min = new BlockVector(Math.min(curr.getBlockX(), min.getBlockX()), Math.min(curr.getBlockY(), min.getBlockY()), Math.min(curr.getBlockZ(), min.getBlockZ()));
			}
			if(!totalRegion.contains(max)) {
				createNewRegion = true;
				Vector curr = totalRegion.getMaximumPoint();
				max = new BlockVector(Math.max(curr.getBlockX(), max.getBlockX()), Math.max(curr.getBlockY(), max.getBlockY()), Math.max(curr.getBlockZ(), max.getBlockZ()));
			}
			if(createNewRegion) {
				totalRegion = new CuboidRegion(min, max);
				occupiedChunks.addAll(totalRegion.getChunks());
				if(afterSetup) game.getWorldManager().loadChunks(this, location.getWorld(), occupiedChunks);
			}
		}
		schematic.paste(editSession, vectorLocation, air);
	}
	
	public void modifiedPosition(Vector position) {
		if(!totalRegion.contains(position)) {
			Vector min = totalRegion.getMinimumPoint();
			Vector max = totalRegion.getMaximumPoint();
			totalRegion = new CuboidRegion(
					new Vector(Math.min(min.getBlockX(), position.getBlockX()), Math.min(min.getBlockY(), position.getBlockY()), Math.min(min.getBlockZ(), position.getBlockZ())),
					new Vector(Math.max(max.getBlockX(), position.getBlockX()), Math.max(max.getBlockY(), position.getBlockY()), Math.max(max.getBlockZ(), position.getBlockZ())));
		}
	}
	
	public void modifiedPosition(Block block) {
		modifiedPosition(Vector.toBlockPoint(block.getX(), block.getY(), block.getZ()));
	}
	
	public void unload() {
		CustomEntityRegistry.removeAll(spawnedEntities); // removes non custom entities as well
		spawnedEntities.clear();
		editSession.setBlocks(totalRegion, AIR);
		editSession.flushQueue();
		FaweAPI.fixLighting(editSession.getWorld(), totalRegion, editSession.getQueue(), FaweQueue.RelightMode.NONE);
		game.getWorldManager().unloadChunks(this);
		game.getObjectiveManager().clear();
	}
	
	private Region getModifyingRegion(Clipboard clipboard) {
		Vector origin = clipboard.getOrigin(); // TODO origin may not need subtraction
		return new CuboidRegion(vectorLocation.add(clipboard.getMinimumPoint()).subtract(origin).toBlockVector(),
				vectorLocation.add(clipboard.getMaximumPoint()).subtract(origin).toBlockVector());
	}
	
	public void relight() {
		game.getTaskManager().start(() -> ((NMSRelighter)editSession.getQueue().getRelighter()).sendChunks(), 15L); // TODO maybe relight sky again
	}
	
	public boolean isProtected(Block block) {
		if(protectedBlocks.contains(block.getType()) || game.getObjectiveManager().getObjective(block) != null) return true;
		Block up = block.getRelative(BlockFace.UP);
		return up.getState().getData() instanceof Door && protectedBlocks.contains(up.getType());
	}
	
	public <T extends Entity> T spawnedEntity(T entity) {
		spawnedEntities.add(entity);
		return entity;
	}
	
}
