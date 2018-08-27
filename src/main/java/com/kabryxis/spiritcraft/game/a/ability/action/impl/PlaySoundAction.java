package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.action.AutomaticSpiritAbilityAction;
import org.bukkit.Location;
import org.bukkit.Sound;

public class PlaySoundAction extends AutomaticSpiritAbilityAction {
	
	private Sound sound;
	private float volume = 1F;
	private float pitch = 1F;
	
	public PlaySoundAction() {
		super("sound");
		registerSubCommandHandler("sound", true, true, data -> sound = Sound.valueOf(data.toUpperCase()));
		registerSubCommandHandler("volume", false, true, data -> volume = Float.parseFloat(data));
		registerSubCommandHandler("pitch", false, true, data -> pitch = Float.parseFloat(data));
	}
	
	@Override
	public void execute(Location location) {
		location.getWorld().playSound(location, sound, volume, pitch);
	}
	
}
