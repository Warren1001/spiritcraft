package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;

public class ExplodeAction extends AbstractSpiritAbilityAction {
	
	private float power = 1F;
	private boolean setFire = false;
	private boolean destroy = false;
	
	public ExplodeAction(GameObjectManager<AbilityAction> objectManager) { // TODO custom explosion damage handling to get who caused the dmg
		super(objectManager, "explode");
		handleSubCommand("power", false, float.class, f -> power = f);
		handleSubCommand("setsfire", false, boolean.class, b -> setFire = b);
		handleSubCommand("destroys", false, boolean.class, b -> destroy = b);
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		Location loc = trigger.getOptimalLocation(player.getLocation());
		loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, destroy);
	}
	
}
