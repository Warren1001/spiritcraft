package com.kabryxis.spiritcraft.game.a.parse;

import com.kabryxis.kabutils.Worker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandHandler implements ParseHandler {
	
	private static final Map<Class<?>, Function<String, Object>> dataConverters = new HashMap<>();
	
	static {
		Function<String, Object> doubleConverter = Double::parseDouble;
		dataConverters.put(Double.class, doubleConverter);
		dataConverters.put(double.class, doubleConverter);
		Function<String, Object> intConverter = Integer::parseInt;
		dataConverters.put(Integer.class, intConverter);
		dataConverters.put(int.class, intConverter);
		Function<String, Object> floatConverter = Float::parseFloat;
		dataConverters.put(Float.class, floatConverter);
		dataConverters.put(float.class, floatConverter);
		Function<String, Object> booleanConverter = Boolean::parseBoolean;
		dataConverters.put(Boolean.class, booleanConverter);
		dataConverters.put(boolean.class, booleanConverter);
		Function<String, Object> longConverter = Long::parseLong;
		dataConverters.put(Long.class, longConverter);
		dataConverters.put(long.class, longConverter);
		Function<String, Object> byteConverter = string -> (byte)intConverter.apply(string);
		dataConverters.put(Byte.class, byteConverter);
		dataConverters.put(byte.class, byteConverter);
	}
	
	public static void registerDataConverter(Class<?> clazz, Function<String, Object> converter) {
		dataConverters.put(clazz, converter);
	}
	
	private final Map<String, SubCommandHandler> subCommandHandlers = new HashMap<>();
	private final Set<Worker> finishedActions = new HashSet<>();
	
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
	
	@SuppressWarnings("unchecked")
	public <T> void registerSubCommandHandler(String subCommand, boolean required, Class<T> objectType, Consumer<T> commandDataHandler) {
		if(objectType == String.class) registerSubCommandHandler(subCommand, required, true, (Consumer<String>)commandDataHandler);
		else {
			Function<String, Object> converter = dataConverters.get(objectType);
			if(converter == null) System.out.println("Could not find a " + objectType.getClass().getSimpleName() + " data converter.");
			else registerSubCommandHandler(subCommand, required, true, string -> commandDataHandler.accept((T)converter.apply(string)));
		}
	}
	
	public void onFinish(Worker worker) {
		finishedActions.add(worker);
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
		finishedActions.forEach(Worker::work);
	}
	
}
