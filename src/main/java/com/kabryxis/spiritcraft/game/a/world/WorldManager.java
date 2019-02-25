package com.kabryxis.spiritcraft.game.a.world;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.serialization.WorldCreatorSerializer;
import com.kabryxis.kabutils.spigot.world.*;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.a.serialization.LoadingLocationSerializer;
import com.kabryxis.spiritcraft.game.a.world.schematic.RoundWorldDataManager;
import com.kabryxis.spiritcraft.game.a.world.schematic.SchematicManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector2D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldManager implements WorldLoader {

	private final Map<String, WorldCreator> worldCreators = new HashMap<>();
	private final Map<String, ChunkGenerator> chunkGenerators = new HashMap<>();
	private final Map<World, BlockStateManager> blockStateManagers = new HashMap<>();
	private final Map<World, EditSession> slowEditSessions = new HashMap<>();
	private final Map<World, EditSession> fastEditSessions = new HashMap<>();
	
	private final SpiritGame game;
	private final SchematicManager schematicManager;
	private final ArenaManager arenaManager;
	private final ChunkLoader chunkLoader;
	private final MetadataProvider metadataProvider;
	private final Config worldCreatorData;
	private final EditSessionBuilder defaultBuilder;
	private final RoundWorldDataManager worldDataManager;
	
	public WorldManager(SpiritGame game) {
		this.game = game;
		defaultBuilder = new EditSessionBuilder("null").fastmode(true).checkMemory(false).changeSetNull()
				.limitUnlimited().allowedRegionsEverywhere();
		File pluginFolder = game.getPlugin().getDataFolder();
		chunkLoader = new ChunkLoader(game.getPlugin());
		metadataProvider = new MetadataProvider(game.getPlugin());
		WorldCreatorSerializer.registerChunkGenerator("empty", new EmptyGenerator());
		worldCreatorData = new Config(new File(pluginFolder, "worlds.yml"), true);
		worldCreatorData.values().stream().map(WorldCreator.class::cast).forEach(this::setWorldCreator);
		Config.registerSerializer(new LoadingLocationSerializer(this));
		ConfigSection.addDeserializer(Location.class, string -> Locations.deserialize(string, this));
		schematicManager = new SchematicManager(new File(pluginFolder, "schematics" + File.separator + "schematics"));
		//Config.forEachConfig(schematicManager.getFolder(), config -> schematicManager.registerAndAddToRotation(new ArenaSchematic(config)));
		arenaManager = new ArenaManager(this, new File(pluginFolder, "arenas"));
		worldDataManager = new RoundWorldDataManager(this, new File(pluginFolder, "schematics"));
	}
	
	public SpiritGame getGame() {
		return game;
	}
	
	public SchematicManager getSchematicManager() {
		return schematicManager;
	}
	
	public ArenaManager getArenaManager() {
		return arenaManager;
	}
	
	public ChunkLoader getChunkLoader() {
		return chunkLoader;
	}
	
	public MetadataProvider getMetadataProvider() {
		return metadataProvider;
	}

	public BlockStateManager getBlockStateManager(World world) {
		return blockStateManagers.computeIfAbsent(world, w -> new EditSessionBlockStateManager(game, w));
	}

	public EditSession getEditSession(World world, boolean fast) {
		EditSessionBuilder builder = defaultBuilder.world(FaweAPI.getWorld(world.getName()));
		return fast ? fastEditSessions.computeIfAbsent(world, ignore -> builder.fastmode(true).build()) :
				slowEditSessions.computeIfAbsent(world, ignore -> builder.fastmode(false).build());
	}

	public EditSession getEditSession(World world) {
		return getEditSession(world, true);
	}
	
	public RoundWorldDataManager getWorldDataManager() {
		return worldDataManager;
	}
	
	public void setWorldCreator(WorldCreator worldCreator) {
		worldCreators.put(worldCreator.name(), worldCreator);
	}
	
	@Override
	public World getWorld(String name) {
		World world = game.getPlugin().getServer().getWorld(name);
		if(world == null) world = worldCreators.computeIfAbsent(name, WorldCreator::new).createWorld();
		return world;
	}
	
	/*public ArenaData constructArenaData() {
		ArenaSchematic schematic = schematicManager.random();
		Arena arena = arenaManager.random(); // arenaManager.random(schematic);
		return new ArenaData(game, arena, schematic);
	}*/
	
	public void loadChunks(Object key, World world, Set<Vector2D> chunkVectors) {
		chunkLoader.keepInMemory(key, chunkVectors.stream().map(vector -> world.getChunkAt(vector.getBlockX(), vector.getBlockZ())).collect(Collectors.toSet()));
	}
	
	public void loadChunks(Object key, Location loc, int radius) {
		for(int cx = loc.getChunk().getX() - radius; cx <= loc.getChunk().getX() + radius; cx++) {
			for(int cz = loc.getChunk().getZ() - radius; cz <= loc.getChunk().getZ() + radius; cz++) {
				chunkLoader.keepInMemory(key, loc.getWorld().getChunkAt(cx, cz));
			}
		}
	}
	
	public void unloadChunks(Object key) {
		chunkLoader.releaseFromMemory(key);
	}
	
}
