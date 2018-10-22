package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.object.Triggers;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import org.bukkit.Location;

public class ExplodeAction extends SpiritGameObjectAction {
	
	private float power = 1F;
	private boolean setFire = false;
	private boolean destroy = false;
	
	public ExplodeAction(ConfigSection creatorData) {
		super(creatorData, "explode");
		handleSubCommand("power", false, float.class, f -> power = f);
		handleSubCommand("setsfire", false, boolean.class, b -> setFire = b);
		handleSubCommand("destroys", false, boolean.class, b -> destroy = b);
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		Location loc = Triggers.getOptimalLocation(triggerData);
		// TODO signify the next damage event that is caused by an explosion was created by the above spiritplayer, same way as item spawning
		loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, destroy);
	}
	
}
