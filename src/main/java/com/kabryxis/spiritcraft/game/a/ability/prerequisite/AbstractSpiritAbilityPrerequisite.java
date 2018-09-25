package com.kabryxis.spiritcraft.game.a.ability.prerequisite;

import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.a.parse.CommandHandler;

import java.util.function.Consumer;

public abstract class AbstractSpiritAbilityPrerequisite implements AbilityPrerequisite {
	
	protected final GameObjectManager<AbilityPrerequisite> objectManager;
	protected final Game game;
	protected final CommandHandler commandHandler;
	protected final String name;
	
	public AbstractSpiritAbilityPrerequisite(GameObjectManager<AbilityPrerequisite> objectManager, String name) {
		this.objectManager = objectManager;
		this.game = objectManager.getGame();
		this.commandHandler = objectManager.getCommandHandler(this);
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Game getGame() {
		return game;
	}
	
	public void handleSubCommand(String subCommand, boolean required, boolean requiresData, Consumer<String> commandDataHandler) {
		commandHandler.registerSubCommandHandler(subCommand, required, requiresData, commandDataHandler);
	}
	
	public <T> void handleSubCommand(String subCommand, boolean required, Class<T> objectType, Consumer<T> commandDataHandler) {
		commandHandler.registerSubCommandHandler(subCommand, required, objectType, commandDataHandler);
	}
	
}
