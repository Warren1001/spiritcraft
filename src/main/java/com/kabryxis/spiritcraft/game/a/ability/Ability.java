package com.kabryxis.spiritcraft.game.a.ability;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.ArrayList;
import java.util.List;

public class Ability implements AbilityCaller {
	
	private final List<AbilityGroup> triggerGroups = new ArrayList<>();
	
	private final AbilityManager abilityManager;
	private final String name;
	
	public Ability(AbilityManager abilityManager, ConfigSection section) {
		this.abilityManager = abilityManager;
		this.name = section.getName();
		ConfigSection triggersChild = section.getChild("triggers");
		for(ConfigSection triggerChild : triggersChild.getChildren()) {
			triggerGroups.add(new AbilityGroup(abilityManager, this, triggerChild));
		}
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		triggerGroups.forEach(group -> group.triggerSafely(player, trigger));
	}
	
	@Override
	public boolean hasTriggerType(TriggerType triggerType) {
		return triggerGroups.stream().anyMatch(group -> group.hasTriggerType(triggerType));
	}
	
	@Override
	public String getName() {
		return name;
	}
	
}
