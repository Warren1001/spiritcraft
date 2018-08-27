package com.kabryxis.spiritcraft.game.a.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CommandHandler implements ParseHandler {
	
	private final Map<String, SubCommandHandler> subCommandHandlers = new HashMap<>();
	
	private final String command;
	
	public CommandHandler(String command) {
		this.command = command;
	}
	
	public void registerSubCommandHandler(String subCommand, boolean required, boolean requiresData, Consumer<String> commandDataHandler) {
		registerSubCommandHandler(new SubCommandHandler(command, subCommand, required, requiresData, commandDataHandler));
	}
	
	public void registerSubCommandHandler(SubCommandHandler subCommandHandler) {
		subCommandHandlers.put(subCommandHandler.getSubCommand(), subCommandHandler);
	}
	
	@Override
	public void parsed(String command, String data) {
		SubCommandHandler subCommandHandler = subCommandHandlers.get(command);
		if(subCommandHandler == null) {
			System.out.println(this.command + " does not know how to handle subcommand '" + command + "', skipping.");
			return;
		}
		subCommandHandler.handle(data);
	}
	
	@Override
	public void finish() {
		Stream<SubCommandHandler> notHandledStream = subCommandHandlers.values().stream().filter(SubCommandHandler::wasNotHandled);
		if(notHandledStream.count() > 0) {
			StringBuilder builder = new StringBuilder("The command '" + command + "' requires the following subcommand(s) to be defined for the command to function: ");
			notHandledStream.forEach(subCommandHandler -> {
				builder.append(subCommandHandler.getSubCommand());
				builder.append(",");
			});
			builder.deleteCharAt(builder.lastIndexOf(","));
			subCommandHandlers.values().forEach(SubCommandHandler::resetHandled);
			throw new IllegalArgumentException(builder.toString());
		}
	}
	
}
