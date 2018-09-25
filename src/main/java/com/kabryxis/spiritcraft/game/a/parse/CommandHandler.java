package com.kabryxis.spiritcraft.game.a.parse;

import com.kabryxis.spiritcraft.game.a.game.object.GameObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandHandler implements ParseHandler {
	
	private static final Map<Class<?>, Function<String, ?>> dataConverters = new HashMap<>();
	
	static {
		dataConverters.put(Double.class, Double::parseDouble);
		dataConverters.put(double.class, Double::parseDouble);
		dataConverters.put(Integer.class, Integer::parseInt);
		dataConverters.put(int.class, Integer::parseInt);
		dataConverters.put(Float.class, Float::parseFloat);
		dataConverters.put(float.class, Float::parseFloat);
		dataConverters.put(Boolean.class, Boolean::parseBoolean);
		dataConverters.put(boolean.class, Boolean::parseBoolean);
		dataConverters.put(Long.class, Long::parseLong);
		dataConverters.put(long.class, Long::parseLong);
		dataConverters.put(Byte.class, Integer::parseInt);
		dataConverters.put(byte.class, Integer::parseInt);
	}
	
	public static <T> void registerDataConverter(Class<T> clazz, Function<String, T> converter) {
		dataConverters.put(clazz, converter);
	}
	
	private final Map<String, SubCommandHandler> subCommandHandlers = new HashMap<>();
	
	private final String command;
	private final String className;
	
	public CommandHandler(GameObject object) {
		this.command = object.getName();
		this.className = object.getClass().getSimpleName();
	}
	
	public void registerSubCommandHandler(String subCommand, boolean required, boolean requiresData, Consumer<String> commandDataHandler) {
		registerSubCommandHandler(new SubCommandHandler(command, subCommand, required, requiresData, commandDataHandler));
	}
	
	public void registerSubCommandHandler(SubCommandHandler subCommandHandler) {
		subCommandHandlers.put(subCommandHandler.getSubCommand(), subCommandHandler);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void registerSubCommandHandler(String subCommand, boolean required, Class<T> objectType, Consumer<T> commandDataHandler) {
		if(objectType == String.class) registerSubCommandHandler(subCommand, required, true, (Consumer<String>)commandDataHandler);
		else {
			Function<String, ?> converter = dataConverters.get(objectType);
			if(converter == null) System.out.println("Could not find a " + objectType.getClass().getSimpleName() + " data converter.");
			else registerSubCommandHandler(subCommand, required, true, string -> commandDataHandler.accept((T)converter.apply(string)));
		}
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
		Set<SubCommandHandler> notHandled = subCommandHandlers.values().stream().filter(SubCommandHandler::wasNotHandled).collect(Collectors.toSet());
		if(!notHandled.isEmpty()) {
			StringBuilder builder = new StringBuilder("The command '" + command + "' requires the following subcommand(s) to be defined for the command to function: ");
			notHandled.forEach(subCommandHandler -> {
				builder.append(subCommandHandler.getSubCommand());
				builder.append(",");
			});
			builder.deleteCharAt(builder.lastIndexOf(","));
			subCommandHandlers.values().forEach(SubCommandHandler::resetHandled);
			throw new IllegalArgumentException(builder.toString());
		}
	}
	
}
