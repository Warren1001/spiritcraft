package com.kabryxis.spiritcraft.game.a.world.sound;

import com.kabryxis.kabutils.data.file.Files;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.spiritcraft.game.a.world.sound.impl.SpiritSoundPlayer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
	
	private final Map<String, SoundPlayer> soundEffectPlayers = new HashMap<>();
	
	private final File folder;
	
	public SoundManager(File folder) {
		this.folder = folder;
		reloadAll();
	}
	
	public void reloadAll() {
		Files.forEachFileWithEnding(folder, ".yml", file -> {
			Config data = new Config(file);
			data.loadSync();
			registerSoundPlayer(new SpiritSoundPlayer(data));
		});
	}
	
	public void registerSoundPlayer(SoundPlayer soundPlayer) {
		soundEffectPlayers.put(soundPlayer.getName(), soundPlayer);
	}
	
	public SoundPlayer getSoundPlayer(String name) {
		return soundEffectPlayers.get(name);
	}
	
	public void playSound(String name, SoundCause cause) {
		SoundPlayer soundPlayer = soundEffectPlayers.get(name);
		if(soundPlayer == null) {
			System.out.println("Could not find the sound named '" + name + "'.");
			return;
		}
		soundPlayer.playSound(cause);
	}
	
}
