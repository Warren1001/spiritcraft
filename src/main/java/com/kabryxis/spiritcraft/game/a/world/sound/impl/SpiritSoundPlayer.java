package com.kabryxis.spiritcraft.game.a.world.sound.impl;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundCause;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;

public class SpiritSoundPlayer implements SoundPlayer {
	
	private final String name;
	private final Sound sound;
	private final float volume;
	private final float pitch;
	private final boolean ghost;
	private final boolean quiet;
	
	public SpiritSoundPlayer(ConfigSection section) {
		this.name = section.getName();
		this.sound = Sound.valueOf(section.get("sound", String.class).toUpperCase());
		this.volume = section.get("volume", Float.class, 1F);
		this.pitch = section.get("pitch", Float.class, 1F);
		this.ghost = section.get("ghost", Boolean.class, false);
		this.quiet = section.get("quiet", Boolean.class, true);
	}
	
	public SpiritSoundPlayer(String name, Sound sound, float volume, float pitch, boolean ghost, boolean quiet) {
		this.name = name;
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
		this.ghost = ghost;
		this.quiet = quiet;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void playSound(SoundCause cause) {
		playSound(cause.getLocation(), sound, false && ghost && quiet ? volume / 2 : volume, pitch);
	} // TODO quiet ghost
	
	public void playSound(Location location, Sound sound, float volume, float pitch) {
		System.out.println("sound:" + sound.name() + ",volume:" + volume + ",pitch:" + pitch);
		location.getWorld().playSound(location, sound, volume, pitch);
	}
	
}
