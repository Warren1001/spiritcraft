package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.action.AutomaticSpiritAbilityAction;
import org.bukkit.Location;

public class ExplodeAction extends AutomaticSpiritAbilityAction {
	
	private float power = 1F;
	private boolean setFire = false;
	private boolean destroy = false;
	
	public ExplodeAction() { // TODO custom explosion damage handling to get who caused the dmg
		super("explode");
		registerSubCommandHandler("power", false, true, data -> power = Float.parseFloat(data));
		registerSubCommandHandler("setsfire", false, false, data -> setFire = true);
		registerSubCommandHandler("destroys", false, false, data -> destroy = true);
	}
	
	@Override
	public void execute(Location loc) {
		loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, destroy);
	}
	
}
