package com.kabryxis.spiritcraft.game.a.ability;

import com.kabryxis.spiritcraft.game.a.parse.CommandHandler;
import com.kabryxis.spiritcraft.game.a.parse.Parsing;

public class AbilityParseHandler implements Parsing {
	
	private final CommandHandler commandHandler;
	
	public AbilityParseHandler(String name) {
		this.commandHandler = new CommandHandler(name);
	}
	
	@Override
	public CommandHandler getParseHandler() {
		return commandHandler;
	}
	
}
