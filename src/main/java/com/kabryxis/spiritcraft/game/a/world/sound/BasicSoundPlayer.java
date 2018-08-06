package com.kabryxis.spiritcraft.game.a.world.sound;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import org.bukkit.Location;
import org.bukkit.Sound;

public class BasicSoundPlayer implements SoundPlayer {
	
	protected final String name;
	protected Sound sound;
	protected float volume;
	protected float pitch;
	
	public BasicSoundPlayer(ConfigSection section) {
		this.name = section.getName();
		this.sound = Sound.valueOf(section.get("sound", String.class).toUpperCase());
		this.volume = section.get("volume", Float.class, 1F);
		this.pitch = section.get("pitch", Float.class, 0.1F);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void playSound(SpiritPlayer causer) {
		Location loc = causer.getLocation();
		loc.getWorld().playSound(loc, sound, volume, pitch);
	}
	
}
