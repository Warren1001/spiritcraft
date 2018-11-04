package com.kabryxis.spiritcraft.game.a.world.sound.impl;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundCause;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundPlayer;
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
		this.sound = section.getEnum("sound", Sound.class);
		this.volume = section.getFloat("volume", 1F);
		this.pitch = section.getFloat("pitch", 1F);
		this.ghost = section.getBoolean("ghost", false);
		this.quiet = section.getBoolean("quiet", true);
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
		cause.playSound(sound, cause.hasOwner() && /*cause.getOwner().isQuietGhost*/false && ghost && quiet ? volume / 2 : volume, pitch);
	} // TODO quiet ghost
	
}
