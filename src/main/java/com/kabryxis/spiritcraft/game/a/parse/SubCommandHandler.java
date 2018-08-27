package com.kabryxis.spiritcraft.game.a.parse;

import org.apache.commons.lang3.Validate;

import java.util.function.Consumer;

public class SubCommandHandler {
	
	private final String command;
	private final String subCommand;
	private final boolean required;
	private final boolean requiresData;
	private final Consumer<String> dataHandler;
	
	private boolean handled = false;
	
	public SubCommandHandler(String command, String subCommand, boolean required, boolean requiresData, Consumer<String> dataHandler) {
		this.command = command;
		this.subCommand = subCommand;
		this.required = required;
		this.requiresData = requiresData;
		this.dataHandler = dataHandler;
	}
	
	public String getSubCommand() {
		return subCommand;
	}
	
	public void resetHandled() {
		handled = false;
	}
	
	public boolean wasNotHandled() {
		return required && !handled;
	}
	
	public void handle(String data) {
		handled = true;
		if(requiresData) Validate.isTrue(data != null && !data.isEmpty(), command + "'s " + subCommand + " subcommand requires data.");
		dataHandler.accept(data);
	}
	
}
