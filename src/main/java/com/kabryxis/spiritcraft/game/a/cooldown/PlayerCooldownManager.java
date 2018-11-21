package com.kabryxis.spiritcraft.game.a.cooldown;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class PlayerCooldownManager implements CooldownManager {
	
	private static final Map<Integer, Supplier<? extends Cooldown>> ABIID_COOLDOWN_SUPPLIER_OVERRIDES = new HashMap<>();
	
	public static void registerAbilityIdCooldownSupplierOverride(int abilityId, Supplier<? extends Cooldown> supplier) {
		ABIID_COOLDOWN_SUPPLIER_OVERRIDES.put(abilityId, supplier);
	}
	
	private final Map<Integer, Cooldown> cooldownMap = new HashMap<>();
	
	private Function<Integer, ? extends Cooldown> defaultSupplier;
	
	public PlayerCooldownManager() {}
	
	public PlayerCooldownManager(Function<Integer, ? extends Cooldown> defaultSupplier) {
		setDefaultSupplier(defaultSupplier);
	}
	
	public void setDefaultSupplier(Function<Integer, ? extends Cooldown> defaultSupplier) {
		this.defaultSupplier = defaultSupplier;
	}
	
	@Override
	public void startCooldown(CooldownEntry entry) {
		if(entry.cooldown <= 0L) return;
		int abilityId = entry.triggerData.getInt("abilityId");
		if(abilityId == 0) return;
		Cooldown cooldown = cooldownMap.get(abilityId);
		if(cooldown == null) {
			Supplier<? extends Cooldown> supplier = ABIID_COOLDOWN_SUPPLIER_OVERRIDES.get(abilityId);
			if(supplier != null) cooldown = supplier.get();
			else if(defaultSupplier != null) cooldown = defaultSupplier.apply(abilityId);
			else throw new IllegalArgumentException(String.format("Could not find cooldown registered to ability id %s and a default supplier was not supplied", abilityId));
			cooldownMap.put(abilityId, cooldown);
		}
		else if(cooldown.isActive()) throw new IllegalStateException("Cannot start cooldown that is already active");
		cooldown.start(entry.cooldown);
	}
	
	@Override
	public boolean isCooldownActive(ConfigSection triggerData) {
		return isCooldownActive(triggerData.getInt("abilityId"));
	}

	@Override
	public boolean isCooldownActive(int abilityId) {
		Cooldown cooldown = cooldownMap.get(abilityId);
		return cooldown != null && cooldown.isActive();
	}
	
}
