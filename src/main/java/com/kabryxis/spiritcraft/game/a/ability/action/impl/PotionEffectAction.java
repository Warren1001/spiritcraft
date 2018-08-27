package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectAction extends AbstractSpiritAbilityAction {
	
	private PotionEffectType type;
	private int amplifier = 1;
	private int duration = 20;
	private boolean force = false;
	
	public PotionEffectAction() {
		super("potion", TriggerType.values());
		registerSubCommandHandler("type", true, true, data -> type = PotionEffectType.getByName(data.toUpperCase()));
		registerSubCommandHandler("amplifier", false, true, data -> amplifier = Integer.parseInt(data));
		registerSubCommandHandler("duration", false, true, data -> duration = Integer.parseInt(data) * 20);
		registerSubCommandHandler("force", false, false, data -> force = true);
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		player.addPotionEffect(new PotionEffect(type, duration, amplifier), force);
	}
	
}
