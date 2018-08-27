package com.kabryxis.spiritcraft.game.a.ability;

import com.kabryxis.spiritcraft.game.a.parse.CommandHandler;
import com.kabryxis.spiritcraft.game.a.parse.ParseHandler;
import com.kabryxis.spiritcraft.game.a.parse.Parsing;
import com.kabryxis.spiritcraft.game.a.parse.SubCommandHandler;

import java.util.function.Consumer;

public class AbilityParseHandler implements Parsing {
	
	private final CommandHandler commandHandler;
	
	public AbilityParseHandler(String name) {
		this.commandHandler = new CommandHandler(name);
	}
	
	@Override
	public ParseHandler getParseHandler() {
		return commandHandler;
	}
	
	public void registerSubCommandHandler(SubCommandHandler subCommandHandler) {
		commandHandler.registerSubCommandHandler(subCommandHandler);
	}
	
	public void registerSubCommandHandler(String subCommand, boolean required, boolean requiresData, Consumer<String> commandDataHandler) {
		commandHandler.registerSubCommandHandler(subCommand, required, requiresData, commandDataHandler);
	}
	
}
