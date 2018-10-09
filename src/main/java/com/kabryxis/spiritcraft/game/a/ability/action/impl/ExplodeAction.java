package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.SpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;

public class ExplodeAction extends SpiritAbilityAction {
	
	private float power = 1F;
	private boolean setFire = false;
	private boolean destroy = false;
	
	public ExplodeAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "explode");
		handleSubCommand("power", false, float.class, f -> power = f);
		handleSubCommand("setsfire", false, boolean.class, b -> setFire = b);
		handleSubCommand("destroys", false, boolean.class, b -> destroy = b);
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		super.trigger(player, trigger);
		Location loc = trigger.getOptimalLocation(player.getLocation());
		// TODO signify the next damage event that is caused by an explosion was created by the above spiritplayer, same way as item spawning
		loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, destroy);
	}
	
}
