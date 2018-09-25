package com.kabryxis.spiritcraft.game.a.ability.action;

import com.kabryxis.kabutils.data.Arrays;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.a.parse.CommandHandler;

import java.util.function.Consumer;

public abstract class AbstractSpiritAbilityAction implements AbilityAction {
	
	protected final GameObjectManager<AbilityAction> objectManager;
	protected final Game game;
	protected final CommandHandler commandHandler;
	protected final String name;
	private final TriggerType[] supportedTriggerTypes;
	
	public AbstractSpiritAbilityAction(GameObjectManager<AbilityAction> objectManager, String name, TriggerType... supportedTriggerTypes) {
		this.objectManager = objectManager;
		this.game = objectManager.getGame();
		this.commandHandler = objectManager.getCommandHandler(this);
		this.name = name;
		this.supportedTriggerTypes = supportedTriggerTypes.length == 0 ? TriggerType.values() : supportedTriggerTypes;
	}
	
	@Override
	public boolean hasTriggerType(TriggerType triggerType) {
		return Arrays.contains(supportedTriggerTypes, triggerType);
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
