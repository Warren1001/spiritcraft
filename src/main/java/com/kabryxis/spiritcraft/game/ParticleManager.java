package com.kabryxis.spiritcraft.game;

import com.kabryxis.kabutils.data.file.KFiles;
import com.kabryxis.kabutils.data.file.yaml.Config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ParticleManager {

	private final Map<String, GhostParticleInfo> particleDataMap = new HashMap<>();
	
	private final File folder;
	
	public ParticleManager(File folder) {
		this.folder = folder;
		folder.mkdirs();
		loadAll();
	}
	
	public void loadAll() {
		KFiles.forEachFileWithEnding(folder, ".yml", file -> registerParticleData(new GhostParticleInfo(new Config(file, true))));
	}
	
	public void registerParticleData(GhostParticleInfo ghostParticleInfo) {
		particleDataMap.put(ghostParticleInfo.getName(), ghostParticleInfo);
	}
	
	public GhostParticleInfo getParticleData(String name) {
		return particleDataMap.get(name);
	}

}
