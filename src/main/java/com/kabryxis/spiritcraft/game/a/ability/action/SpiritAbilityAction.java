package com.kabryxis.spiritcraft.game.a.ability.action;

import com.kabryxis.kabutils.data.Arrays;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownEntry;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownHandler;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.a.parse.CommandHandler;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.function.Consumer;

public class SpiritAbilityAction implements AbilityAction {
	
	protected final GameObjectManager<AbilityAction> objectManager;
	protected final Game game;
	protected final String name;
	protected final CommandHandler commandHandler;
	private final TriggerType[] supportedTriggerTypes;
	
	protected long cooldown = 0L;
	
	public SpiritAbilityAction(GameObjectManager<AbilityAction> objectManager, String name, TriggerType... supportedTriggerTypes) {
		this.objectManager = objectManager;
		this.game = objectManager.getGame();
		this.name = name;
		this.commandHandler = objectManager.getCommandHandler(this);
		this.supportedTriggerTypes = supportedTriggerTypes.length == 0 ? TriggerType.values() : supportedTriggerTypes;
		handleSubCommand("cooldown", false, long.class, l -> cooldown = l);
	}
	
	@Override
	public boolean hasTriggerType(TriggerType triggerType) {
		return Arrays.containsInstance(supportedTriggerTypes, triggerType);
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		if(cooldown > 0L) trigger.cooldownHandler.setCooldown(new CooldownEntry(CooldownHandler.Priority.LOW, cooldown, trigger));
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
