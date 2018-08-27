package com.kabryxis.spiritcraft.game.a.ability.action.impl.creator;

import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityActionCreator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class BasicAbilityActionCreator implements AbilityActionCreator {
	
	private final Map<String, Function<String, AbilityAction>> creators = new HashMap<>();
	
	public void registerCreator(String command, Function<String, AbilityAction> creator) {
		creators.put(command, creator);
	}
	
	@Override
	public AbilityAction create(String command, String data) {
		return creators.get(command).apply(data);
	}
	
	@Override
	public Set<String> getHandledCommands() {
		return creators.keySet();
	}
	
}
