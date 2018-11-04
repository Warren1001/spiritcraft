package com.kabryxis.spiritcraft.game.a.world.sound;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.spiritcraft.game.a.world.sound.impl.SpiritSoundPlayer;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
	
	private final Map<String, SoundPlayer> soundEffectPlayers = new HashMap<>();
	
	private final Config data;
	
	public SoundManager(File folder) {
		data = new Config(new File(folder, "sounds.yml"));
		data.load();
		//data.values().forEach(value -> System.out.println(value.getClass().getName()));
		data.getChildren().forEach(child -> registerSoundPlayer(new SpiritSoundPlayer(child)));
	}
	
	public void registerSoundPlayer(SoundPlayer soundPlayer) {
		soundEffectPlayers.put(soundPlayer.getName(), soundPlayer);
		//System.out.println(String.format("Registered '%s' SoundPlayer", soundPlayer.getName()));
	}
	
	public SoundPlayer getSoundPlayer(String name) {
		return soundEffectPlayers.get(name);
	}
	
	public void playSound(String name, SoundCause cause) {
		Validate.notNull(soundEffectPlayers.get(name), "Could not find the sound named '%s'", name).playSound(cause);
	}
	
}
