package com.kabryxis.spiritcraft.game.a.objective.action;

import com.kabryxis.spiritcraft.game.a.world.ArenaData;
import org.bukkit.block.Block;

public interface ObjectiveActionCreator {
	
	ObjectiveAction create(ArenaData arenaData, Block location, String data);
	
}
