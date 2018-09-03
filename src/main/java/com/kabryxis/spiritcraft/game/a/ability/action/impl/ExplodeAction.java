package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;

public class ExplodeAction extends AbstractSpiritAbilityAction {
	
	private float power = 1F;
	private boolean setFire = false;
	private boolean destroy = false;
	
	public ExplodeAction() { // TODO custom explosion damage handling to get who caused the dmg
		super("explode");
		getParseHandler().registerSubCommandHandler("power", false, float.class, f -> power = f);
		getParseHandler().registerSubCommandHandler("setsfire", false, boolean.class, b -> setFire = b);
		getParseHandler().registerSubCommandHandler("destroys", false, boolean.class, b -> destroy = b);
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		Location loc = trigger.getOptimalLocation(player.getLocation());
		loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, destroy);
	}
	
}
