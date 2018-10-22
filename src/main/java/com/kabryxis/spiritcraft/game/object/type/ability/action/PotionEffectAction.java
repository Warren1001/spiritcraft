package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.object.Triggers;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectAction extends SpiritGameObjectAction {
	
	private PotionEffectType type;
	private int amplifier = 1;
	private int duration = 20;
	private boolean force = false;
	
	public PotionEffectAction(ConfigSection creatorData) {
		super(creatorData, "potion");
		handleSubCommand("type", true, true, data -> type = PotionEffectType.getByName(data.toUpperCase()));
		handleSubCommand("amplifier", false, int.class, i -> amplifier = i);
		handleSubCommand("duration", false, int.class, i -> duration = i * 20);
		handleSubCommand("force", false, boolean.class, b -> force = b);
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		Triggers.getOptimalTarget(triggerData).addPotionEffect(new PotionEffect(type, duration, amplifier), force);
	}
	
}
