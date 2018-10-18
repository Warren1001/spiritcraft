package com.kabryxis.spiritcraft.game;

import com.kabryxis.kabutils.data.file.Files;
import com.kabryxis.kabutils.data.file.yaml.Config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ParticleManager {

	private final Map<String, ParticleData> particleDataMap = new HashMap<>();
	
	private final File folder;
	
	public ParticleManager(File folder) {
		this.folder = folder;
		folder.mkdirs();
		loadAll();
	}
	
	public void loadAll() {
		Files.forEachFileWithEnding(folder, ".yml", file -> registerParticleData(new ParticleData(new Config(file, true))));
	}
	
	public void registerParticleData(ParticleData particleData) {
		particleDataMap.put(particleData.getName(), particleData);
	}
	
	public ParticleData getParticleData(String name) {
		return particleDataMap.get(name);
	}

}
