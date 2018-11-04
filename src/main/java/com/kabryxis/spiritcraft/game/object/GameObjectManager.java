package com.kabryxis.spiritcraft.game.object;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.a.parse.CommandHandler;
import com.kabryxis.spiritcraft.game.a.parse.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GameObjectManager<T extends GameObject> {
	
	private final Map<String, Function<ConfigSection, T>> gameObjectCreators = new HashMap<>();
	private final Map<GameObject, CommandHandler> commandHandlerMap = new HashMap<>();
	private final Map<String, T> cacheMap = new HashMap<>();
	
	private final SpiritGame game;
	private final Parser parser;
	private final Class<? extends T> clazz;
	
	public GameObjectManager(SpiritGame game, Class<? extends T> clazz) {
		this.game = game;
		this.parser = game.getParser();
		this.clazz = clazz;
	}
	
	public SpiritGame getGame() {
		return game;
	}
	
	public void registerCreator(String name, Function<ConfigSection, T> creator) {
		gameObjectCreators.put(name, creator);
	}
	
	public T get(String data, ConfigSection creatorData) {
		creatorData.put("objectManager", this);
		T object = cacheMap.get(data);
		if(object == null) {
			String[] dataArray = parser.splitData(data);
			String name = dataArray[0];
			Function<ConfigSection, T> creator = gameObjectCreators.get(name);
			if(creator == null) throw new IllegalArgumentException("Could not find a creator for the " + clazz.getSimpleName() + " '" + name + "'.");
			object = creator.apply(creatorData);
			parser.parse(dataArray[1], getCommandHandler(object));
			cacheMap.put(data, object);
		}
		return object;
	}
	
	public T get(String data) {
		return cacheMap.get(data);
	}
	
	public CommandHandler getCommandHandler(GameObject object) {
		return commandHandlerMap.computeIfAbsent(object, CommandHandler::new);
	}
	
	public void clear() {
		cacheMap.clear();
	}
	
}
