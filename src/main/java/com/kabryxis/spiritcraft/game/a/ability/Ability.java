package com.kabryxis.spiritcraft.game.a.ability;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownEntry;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.util.List;
import java.util.stream.Collectors;

public class Ability implements AbilityCaller {
	
	private final List<AbilityGroup> triggerGroups;
	private final AbilityManager abilityManager;
	private final String name;
	private final long cooldown;
	
	public Ability(AbilityManager abilityManager, ConfigSection section) {
		this.abilityManager = abilityManager;
		this.name = section.getName();
		this.triggerGroups = section.get("triggers", ConfigSection.class).getChildren().stream().map(child -> new AbilityGroup(abilityManager, this, child)).collect(Collectors.toList());
		this.cooldown = section.get("cooldown", long.class, 0L);
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		if(cooldown > 0L) trigger.cooldownHandler.setCooldown(new CooldownEntry(cooldown, trigger));
		triggerGroups.forEach(group -> group.trigger(player, trigger));
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
