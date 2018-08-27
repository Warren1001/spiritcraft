package com.kabryxis.spiritcraft.game.a.objective.action;

import com.kabryxis.spiritcraft.game.a.world.DimData;
import org.bukkit.block.Block;

public interface ObjectiveActionCreator {
	
	ObjectiveAction create(DimData dimData, Block location, String data);
	
}
