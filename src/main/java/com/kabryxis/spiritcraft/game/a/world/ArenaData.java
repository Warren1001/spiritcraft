package com.kabryxis.spiritcraft.game.a.world;

import com.boydti.fawe.FaweCache;
import com.boydti.fawe.example.NMSRelighter;
import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.util.EditSessionBuilder;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
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
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Objects;
import java.util.Set;

public class ArenaData {
	
	private static final BaseBlock AIR = FaweCache.getBlock(0, 0);
	
	private final Game game;
	private final Arena arena;
	private final Set<BlockVector2D> occupiedChunks;
	private final ArenaSchematic schematic;
	private final EditSession editSession;
	private final RandomArrayList<Location> ghostSpawns, hunterSpawns;
	
	private Region totalRegion;
	
	public ArenaData(Game game, Arena arena, ArenaSchematic schematic) {
		this.game = game;
		this.arena = arena;
		this.occupiedChunks = arena.getOccupiedChunks();
		this.schematic = schematic;
		this.editSession = new EditSessionBuilder(arena.getLocation().getWorld().getName()).fastmode(true).checkMemory(false)
				.changeSetNull().limitUnlimited().allowedRegionsEverywhere().build();
		this.ghostSpawns = schematic.getData().getList("spawns.ghost", String.class).stream().map(string -> Locations.deserialize(arena.getLocation().getWorld(), string)).collect(() ->
				new RandomArrayList<>(Integer.MAX_VALUE), RandomArrayList::add, RandomArrayList::addAll);
		this.hunterSpawns = schematic.getData().getList("spawns.hunter", String.class).stream().map(string -> Locations.deserialize(arena.getLocation().getWorld(), string)).collect(() ->
				new RandomArrayList<>(Integer.MAX_VALUE), RandomArrayList::add, RandomArrayList::addAll);
		ConfigSection objectivesChild = schematic.getData().getChild("objectives");
		if(objectivesChild != null) game.getObjectiveManager().loadObjectives(objectivesChild);
		Region region = getModifyingRegion(Objects.requireNonNull(schematic.getSchematic().getClipboard()));
		for(int cx = (region.getMinimumPoint().getBlockX() >> 4) - 1; cx <= (region.getMaximumPoint().getBlockX()) + 1; cx++) {
			for(int cz = (region.getMinimumPoint().getBlockZ() >> 4) - 1; cz <= (region.getMaximumPoint().getBlockZ()) + 1; cz++) {
				occupiedChunks.add(new BlockVector2D(cx, cz));
			}
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
		return game.getObjectiveManager().getObjective(location);
	}
	
	public Location getRandomGhostSpawn() {
		return ghostSpawns.random();
	}
	
	public Location getRandomHunterSpawn() {
		return hunterSpawns.random();
	}
	
	public void load() {
		game.getWorldManager().loadChunks(this, arena.getLocation().getWorld(), occupiedChunks);
		paste0(schematic, false, true);
		BukkitThreads.syncLater(() -> { // relighting needs to be after all blocks are completely set or some chunks will be improperly lit, idk how better to do this.
			editSession.getQueue().getRelighter().fixSkyLighting();
			game.finishSetup();
		}, 60L);
		// lighting bug with corner blocks (or possibly transparent blocks like doors and stairs), light isnt accurately recalculated unless player has the chunk loaded
		// either design maps with no corner blocks or find out how to relight without lag while players occupy the chunks
	}
	
	public void pasteAnotherSchematic(SchematicWrapper schematic, boolean air) {
		paste0(schematic, air, false);
	}
	
	private void paste0(SchematicWrapper schematicWrapper, boolean air, boolean callFinish) {
		Schematic schematic = schematicWrapper.getSchematic();
		Region region = getModifyingRegion(Objects.requireNonNull(schematic.getClipboard()));
		Vector min = region.getMinimumPoint();
		Vector max = region.getMaximumPoint();
		if(totalRegion == null) totalRegion = region;
		else {
			boolean createNewRegion = false;
			if(!totalRegion.contains(min)) {
				createNewRegion = true;
				Vector curr = totalRegion.getMinimumPoint();
				min = new Vector(Math.min(curr.getBlockX(), min.getBlockX()), Math.min(curr.getBlockY(), min.getBlockY()), Math.min(curr.getBlockZ(), min.getBlockZ()));
			}
			if(!totalRegion.contains(max)) {
				createNewRegion = true;
				Vector curr = totalRegion.getMaximumPoint();
				max = new Vector(Math.max(curr.getBlockX(), max.getBlockX()), Math.max(curr.getBlockY(), max.getBlockY()), Math.max(curr.getBlockZ(), max.getBlockZ()));
			}
			if(createNewRegion) totalRegion = new CuboidRegion(min, max);
		}
		schematic.paste(editSession, arena.getVectorLocation(), air);
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
		editSession.setBlocks(totalRegion, AIR);
		editSession.flushQueue();
		game.getWorldManager().unloadChunks(this);
	}
	
	private Region getModifyingRegion(Clipboard clipboard) {
		Vector loc = arena.getVectorLocation();
		Vector origin = clipboard.getOrigin();
		return new CuboidRegion(loc.add(clipboard.getMinimumPoint()).subtract(origin).toBlockVector(),
				loc.add(clipboard.getMaximumPoint()).subtract(origin).toBlockVector());
	}
	
	public void relight() {
		BukkitThreads.syncLater(() -> ((NMSRelighter)editSession.getQueue().getRelighter()).sendChunks(), 15L); // TODO maybe relight sky again
	}
	
}
