package com.kabryxis.spiritcraft.game.a.item;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;

public class BasicAbilityItem implements AbilityItem {
	
	//private final String name;
	
	public BasicAbilityItem(ConfigSection data) {
	
	}
	
	@Override
	public boolean use(UseType type) {
		return false;
	}
	
}
