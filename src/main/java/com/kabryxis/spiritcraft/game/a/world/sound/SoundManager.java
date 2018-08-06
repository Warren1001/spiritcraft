package com.kabryxis.spiritcraft.game.a.world.sound;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.data.file.FileEndingFilter;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
	
	private final Map<String, SoundPlayer> soundEffectPlayers = new HashMap<>();
	
	private final File folder;
	
	public SoundManager(File folder) {
		this.folder = folder;
		File[] files = folder.listFiles(new FileEndingFilter(".yml"));
		if(files != null) {
			for(File file : files) {
				Config data = new Config(file);
				data.loadSync();
				registerSoundEffect(data);
			}
		}
	}
	
	public void reloadAll() {
		File[] files = folder.listFiles(new FileEndingFilter(".yml"));
		if(files != null) {
			for(File file : files) {
				new Config(file).load(this::registerSoundEffect);
			}
		}
	}
	
	private void registerSoundEffect(ConfigSection section) {
		registerSoundEffect(section.get("ghost-effect", Boolean.class, false) ? new GhostSoundEffectPlayer(section) : new BasicSoundPlayer(section));
	}
	
	public void registerSoundEffect(SoundPlayer soundPlayer) {
		soundEffectPlayers.put(soundPlayer.getName(), soundPlayer);
	}
	
	public void playSound(String name, SpiritPlayer causer) {
		soundEffectPlayers.get(name).playSound(causer);
	}
	
}
