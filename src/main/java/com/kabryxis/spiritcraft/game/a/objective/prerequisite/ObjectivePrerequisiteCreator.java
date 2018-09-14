package com.kabryxis.spiritcraft.game.a.objective.prerequisite;

import com.kabryxis.spiritcraft.game.a.world.ArenaData;
import org.bukkit.block.Block;

public interface ObjectivePrerequisiteCreator {
	
	ObjectivePrerequisite create(ArenaData arenaData, Block location, String data);
	
}
