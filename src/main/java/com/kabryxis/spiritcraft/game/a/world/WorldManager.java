package com.kabryxis.spiritcraft.game.a.world;

import com.boydti.fawe.util.EditSessionBuilder;
import com.kabryxis.spiritcraft.game.Schematic;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.spigot.world.ChunkLoader;
import com.kabryxis.kabutils.spigot.world.EmptyGenerator;
import com.kabryxis.kabutils.spigot.world.WorldLoader;
import com.sk89q.worldedit.EditSession;
import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WorldManager implements WorldLoader {
	
	private static final int CHUNK_RADIUS = 6;
	
	private final Map<String, WorldCreator> worldCreators = new HashMap<>();
	private final Map<String, EditSession> editSessions = new HashMap<>();
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
		this.worldCreatorData = new Config(new File(pluginFolder, "worlds.yml"));
		chunkGenerators.put("empty", new EmptyGenerator());
		worldCreatorData.loadSync();
		worldCreatorData.getChildren().forEach(child -> {
			String worldName = child.getName();
			WorldCreator worldCreator = new WorldCreator(worldName);
			String environmentString = child.get("environment", String.class);
			if(environmentString != null) worldCreator.environment(World.Environment.valueOf(environmentString.toUpperCase()));
			String typeString = child.get("type", String.class);
			if(typeString != null) worldCreator.type(WorldType.getByName(typeString));
			String generatorString = child.get("generator", String.class);
			if(generatorString != null) worldCreator.generator(chunkGenerators.get(generatorString));
			setWorldCreator(worldName, worldCreator);
		});
		this.schematicManager = new SchematicManager(new File(pluginFolder, "schematics"));
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
	
	public void setWorldCreator(String worldName, WorldCreator worldCreator) {
		worldCreators.put(worldName, worldCreator);
	}
	
	@Override
	public World loadWorld(String name) {
		return worldCreators.getOrDefault(name, new WorldCreator(name)).createWorld();
	}
	
	public World getWorld(String name) {
		World world = game.getPlugin().getServer().getWorld(name);
		if(world == null) world = loadWorld(name);
		return world;
	}
	
	public EditSession getEditSession(World world) {
		return editSessions.computeIfAbsent(world.getName(), name -> new EditSessionBuilder(name).fastmode(true).build());
	}
	
	public ArenaData constructArenaData() {
		Arena arena = arenaManager.random();
		Schematic schematic = schematicManager.random(arena);
		return new ArenaData(game, arena, schematic);
	}
	
	public void loadChunks(Object key, Location center) {
		Chunk centerChunk = center.getChunk();
		for(int cx = -CHUNK_RADIUS; cx <= CHUNK_RADIUS; cx++) {
			for(int cz = -CHUNK_RADIUS; cz <= CHUNK_RADIUS; cz++) {
				Chunk chunk = centerChunk.getWorld().getChunkAt(centerChunk.getX() + cx, centerChunk.getZ() + cz);
				chunkLoader.keepInMemory(key, chunk);
				chunk.load();
			}
		}
	}
	
	public void unloadChunks() {
		chunkLoader.releaseFromMemory(this);
	}
	
}
