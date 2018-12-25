package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.kabutils.random.weighted.WeightedRandomArrayList;

public class RoundWorldDataManager {
	
	private final RandomArrayList<RoundWorldDataContainer> dataContainers = new WeightedRandomArrayList<>();
	
	public RoundWorldData create() {
		return dataContainers.random().create();
	}
	
}
