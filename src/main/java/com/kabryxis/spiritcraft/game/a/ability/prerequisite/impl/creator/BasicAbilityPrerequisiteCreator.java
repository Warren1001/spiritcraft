package com.kabryxis.spiritcraft.game.a.ability.prerequisite.impl.creator;

import com.kabryxis.spiritcraft.game.a.ability.prerequisite.AbilityPrerequisite;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.AbilityPrerequisiteCreator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class BasicAbilityPrerequisiteCreator implements AbilityPrerequisiteCreator {
	
	private final Map<String, Function<String, AbilityPrerequisite>> creators = new HashMap<>();
	
	public void registerCreator(String command, Function<String, AbilityPrerequisite> creator) {
		creators.put(command, creator);
	}
	
	@Override
	public AbilityPrerequisite create(String command, String data) {
		return creators.get(command).apply(data);
	}
	
	@Override
	public Set<String> getHandledCommands() {
		return creators.keySet();
	}
	
}
