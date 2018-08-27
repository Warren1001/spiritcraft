package com.kabryxis.spiritcraft.game.a.objective.prerequisite;

import com.kabryxis.spiritcraft.game.a.world.DimData;
import org.bukkit.block.Block;

public interface ObjectivePrerequisiteCreator {
	
	ObjectivePrerequisite create(DimData dimData, Block location, String data);
	
}
