package com.kabryxis.spiritcraft.game.a.cooldown;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlayerCooldownManager implements CooldownManager {
	
	private final Map<Integer, Cooldown> cooldownMap = new HashMap<>();
	
	private Function<Integer, Cooldown> defaultSupplier;
	
	public PlayerCooldownManager(Function<Integer, Cooldown> defaultSupplier) {
		setDefaultSupplier(defaultSupplier);
	}
	
	public void setDefaultSupplier(Function<Integer, Cooldown> defaultSupplier) {
		this.defaultSupplier = defaultSupplier;
	}
	
	@Override
	public void startCooldown(CooldownEntry entry) {
		if(entry.cooldown <= 0L) return;
		int abilityId = entry.triggerData.get("abilityId");
		Cooldown cooldown = cooldownMap.get(abilityId);
		if(cooldown == null) {
			if(abilityId == 0) return;
			Validate.notNull(defaultSupplier, "Could not find cooldown registered to ability id %s and a default supplier was not supplied", abilityId);
			cooldownMap.put(abilityId, (cooldown = defaultSupplier.apply(abilityId)));
		}
		else if(cooldown.isActive()) throw new IllegalStateException("Cannot start cooldown that is already active");
		cooldown.start(entry.cooldown);
	}
	
	@Override
	public boolean isCooldownActive(ConfigSection triggerData) {
		Cooldown cooldown = cooldownMap.get(triggerData.get("abilityId"));
		return cooldown != null && cooldown.isActive();
	}
	
}
