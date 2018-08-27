package com.kabryxis.spiritcraft.game.a.ability.action;

import java.util.Set;

public interface AbilityActionCreator {
	
	AbilityAction create(String command, String data);
	
	Set<String> getHandledCommands();
	
}
