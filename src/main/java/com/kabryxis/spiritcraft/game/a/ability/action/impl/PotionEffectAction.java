package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
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
		super("potion");
		getParseHandler().registerSubCommandHandler("type", true, true, data -> type = PotionEffectType.getByName(data.toUpperCase()));
		getParseHandler().registerSubCommandHandler("amplifier", false, int.class, i -> amplifier = i);
		getParseHandler().registerSubCommandHandler("duration", false, int.class, i -> duration = i * 20);
		getParseHandler().registerSubCommandHandler("force", false, boolean.class, b -> force = b);
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		player.addPotionEffect(new PotionEffect(type, duration, amplifier), force);
	}
	
}
