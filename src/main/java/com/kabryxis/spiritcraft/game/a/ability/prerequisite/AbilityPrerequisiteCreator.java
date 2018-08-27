package com.kabryxis.spiritcraft.game.a.ability.prerequisite;

import java.util.Set;

public interface AbilityPrerequisiteCreator {
	
	AbilityPrerequisite create(String command, String data);
	
	Set<String> getHandledCommands();
	
}
