package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectAction extends AbstractSpiritAbilityAction {
	
	private PotionEffectType type;
	private int amplifier = 1;
	private int duration = 20;
	private boolean force = false;
	
	public PotionEffectAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "potion");
		handleSubCommand("type", true, true, data -> type = PotionEffectType.getByName(data.toUpperCase()));
		handleSubCommand("amplifier", false, int.class, i -> amplifier = i);
		handleSubCommand("duration", false, int.class, i -> duration = i * 20);
		handleSubCommand("force", false, boolean.class, b -> force = b);
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		player.addPotionEffect(new PotionEffect(type, duration, amplifier), force);
	}
	
}
