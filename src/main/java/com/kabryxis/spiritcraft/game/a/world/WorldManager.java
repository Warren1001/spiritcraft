package com.kabryxis.spiritcraft.game.a.world;

import com.kabryxis.kabutils.data.file.Files;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.spigot.serialization.WorldCreatorSerializer;
import com.kabryxis.kabutils.spigot.world.ChunkLoader;
import com.kabryxis.kabutils.spigot.world.EmptyGenerator;
import com.kabryxis.kabutils.spigot.world.WorldLoader;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.a.serialization.LoadingLocationSerializer;
import com.kabryxis.spiritcraft.game.a.world.schematic.ArenaSchematic;
import com.kabryxis.spiritcraft.game.a.world.schematic.SchematicManager;
import com.sk89q.worldedit.BlockVector2D;
import org.bukkit.Bukkit;
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
	
	private final Game game;
	private final SchematicManager schematicManager;
	private final ArenaManager arenaManager;
	private final ChunkLoader chunkLoader;
	private final MetadataProvider metadataProvider;
	private final Config worldCreatorData;
	
	public WorldManager(Game game) {
		this.game = game;
		File pluginFolder = game.getPlugin().getDataFolder();
		this.chunkLoader = new ChunkLoader(game.getPlugin());
		this.metadataProvider = new MetadataProvider(game.getPlugin());
		WorldCreatorSerializer.registerChunkGenerator("empty", new EmptyGenerator());
		this.worldCreatorData = new Config(new File(pluginFolder, "worlds.yml"), true);
		worldCreatorData.values().stream().map(WorldCreator.class::cast).forEach(this::setWorldCreator);
		Config.registerSerializer(new LoadingLocationSerializer(this));
		this.schematicManager = new SchematicManager(new File(pluginFolder, "schematics"));
		Files.forEachFileWithEnding(schematicManager.getFolder(), Config.EXTENSION, file -> schematicManager.registerAndAddToRotation(new ArenaSchematic(new Config(file, true))));
		this.arenaManager = new ArenaManager(this, new File(pluginFolder, "arenas"));
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
	
	public void setWorldCreator(WorldCreator worldCreator) {
		worldCreators.put(worldCreator.name(), worldCreator);
	}
	
	@Override
	public World getWorld(String name) {
		World world = Bukkit.getWorld(name);
		if(world == null) world = worldCreators.computeIfAbsent(name, WorldCreator::new).createWorld();
		return world;
	}
	
	public ArenaData constructArenaData() {
		Arena arena = arenaManager.random();
		ArenaSchematic schematic = schematicManager.random(arena);
		return new ArenaData(game, arena, schematic);
	}
	
	public void loadChunks(Object key, World world, Set<BlockVector2D> chunkVectors) {
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
