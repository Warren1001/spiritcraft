package com.kabryxis.spiritcraft.game.a.ability.prerequisite.impl.creator;

import com.kabryxis.spiritcraft.game.a.ability.prerequisite.AbilityParsingAbilityPrerequisite;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.AbilityPrerequisiteCreator;
import com.kabryxis.spiritcraft.game.a.parse.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class SpiritAbilityPrerequisiteCreator implements AbilityPrerequisiteCreator {
	
	private final Map<String, Supplier<AbilityParsingAbilityPrerequisite>> creators = new HashMap<>();
	
	private final Parser parser;
	
	public SpiritAbilityPrerequisiteCreator(Parser parser) {
		this.parser = parser;
	}
	
	public void registerCreator(String command, Supplier<AbilityParsingAbilityPrerequisite> creator) {
		creators.put(command, creator);
	}
	
	@Override
	public AbilityParsingAbilityPrerequisite create(String command, String data) {
		AbilityParsingAbilityPrerequisite prerequisite = creators.get(command).get();
		parser.parse(data, prerequisite.getParseHandler());
		return prerequisite;
	}
	
	@Override
	public Set<String> getHandledCommands() {
		return creators.keySet();
	}
	
}
