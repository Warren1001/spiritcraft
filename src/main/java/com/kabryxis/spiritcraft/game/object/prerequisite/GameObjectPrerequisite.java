package com.kabryxis.spiritcraft.game.object.prerequisite;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.object.GameObject;

public interface GameObjectPrerequisite extends GameObject {
	
	boolean canPerform(ConfigSection triggerData);
	
	boolean perform(ConfigSection triggerData);
	
}
