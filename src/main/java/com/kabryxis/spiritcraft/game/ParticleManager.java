package com.kabryxis.spiritcraft.game;

import com.kabryxis.kabutils.data.file.FileEndingFilter;
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
		File[] files = folder.listFiles(new FileEndingFilter(".yml"));
		if(files != null && files.length != 0) {
			particleDataMap.clear();
			for(File file : files) {
				new Config(file).load(data -> registerParticleData(new ParticleData(data)));
			}
		}
	}
	
	public void registerParticleData(ParticleData particleData) {
		particleDataMap.put(particleData.getName(), particleData);
	}
	
	public ParticleData getParticleData(String name) {
		return particleDataMap.get(name);
	}

}
