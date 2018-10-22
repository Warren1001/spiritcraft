package com.kabryxis.spiritcraft.game.object.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.object.GameObject;
import com.kabryxis.spiritcraft.game.object.TriggerType;

public interface GameObjectAction extends GameObject {
	
	boolean hasTriggerType(TriggerType type);
	
	default boolean canPerform(ConfigSection triggerData) {
		TriggerType type = triggerData.get("type");
		return type != null && hasTriggerType(type);
	}
	
	void perform(ConfigSection triggerData);
	
}
