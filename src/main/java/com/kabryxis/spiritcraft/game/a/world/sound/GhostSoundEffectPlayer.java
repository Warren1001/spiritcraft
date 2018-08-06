package com.kabryxis.spiritcraft.game.a.world.sound;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import org.bukkit.Location;

public class GhostSoundEffectPlayer extends BasicSoundPlayer {
	
	public GhostSoundEffectPlayer(ConfigSection section) {
		super(section);
	}
	
	@Override
	public void playSound(SpiritPlayer causer) {
		if(isQuietGhost(causer)) {
			Location loc = causer.getLocation();
			loc.getWorld().playSound(loc, sound, volume / 2, pitch);
		}
		else super.playSound(causer);
	}
	
	private boolean isQuietGhost(SpiritPlayer player) {
		return false; // TODO
	}
	
}
