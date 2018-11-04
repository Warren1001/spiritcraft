package com.kabryxis.spiritcraft.game.object;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.a.parse.CommandHandler;

import java.util.function.Consumer;

public abstract class SpiritGameObject implements GameObject {
	
	protected final GameObjectManager<? extends GameObject> objectManager;
	protected final SpiritGame game;
	protected final CommandHandler commandHandler;
	protected final String name;
	
	public SpiritGameObject(ConfigSection creatorData, String name) {
		this.objectManager = creatorData.get("objectManager");
		this.game = objectManager.getGame();
		this.commandHandler = objectManager.getCommandHandler(this);
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public SpiritGame getGame() {
		return game;
	}
	
	public void handleSubCommand(String subCommand, boolean required, boolean requiresData, Consumer<String> commandDataHandler) {
		commandHandler.registerSubCommandHandler(subCommand, required, requiresData, commandDataHandler);
	}
	
	public <T> void handleSubCommand(String subCommand, boolean required, Class<T> objectType, Consumer<T> commandDataHandler) {
		commandHandler.registerSubCommandHandler(subCommand, required, objectType, commandDataHandler);
	}
	
}
