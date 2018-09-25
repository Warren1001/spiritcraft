package com.kabryxis.spiritcraft.game.a.objective.action;

import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.a.objective.Objective;
import com.kabryxis.spiritcraft.game.a.parse.CommandHandler;
import org.bukkit.event.Event;

import java.util.function.Consumer;

public abstract class AbstractSpiritObjectiveAction implements ObjectiveAction {
	
	protected final GameObjectManager<ObjectiveAction> objectManager;
	protected final Game game;
	protected final CommandHandler commandHandler;
	protected final String name;
	
	public AbstractSpiritObjectiveAction(GameObjectManager<ObjectiveAction> objectManager, String name) {
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
	
	@Override
	public void event(Objective objective, Event event) {}
	
	public void handleSubCommand(String subCommand, boolean required, boolean requiresData, Consumer<String> commandDataHandler) {
		commandHandler.registerSubCommandHandler(subCommand, required, requiresData, commandDataHandler);
	}
	
	public <T> void handleSubCommand(String subCommand, boolean required, Class<T> objectType, Consumer<T> commandDataHandler) {
		commandHandler.registerSubCommandHandler(subCommand, required, objectType, commandDataHandler);
	}
	
}
