package com.kabryxis.spiritcraft.game.a.ability.action.impl.creator;

import com.kabryxis.spiritcraft.game.a.ability.action.AbilityActionCreator;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.parse.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class SpiritAbilityActionCreator implements AbilityActionCreator {
	
	private final Map<String, Supplier<AbstractSpiritAbilityAction>> creators = new HashMap<>();
	
	private final Parser parser;
	
	public SpiritAbilityActionCreator(Parser parser) {
		this.parser = parser;
	}
	
	public void registerCreator(String command, Supplier<AbstractSpiritAbilityAction> creator) {
		creators.put(command, creator);
	}
	
	@Override
	public AbstractSpiritAbilityAction create(String command, String data) {
		AbstractSpiritAbilityAction action = creators.get(command).get();
		parser.parse(data, action.getParseHandler());
		return action;
	}
	
	@Override
	public Set<String> getHandledCommands() {
		return creators.keySet();
	}
	
}
