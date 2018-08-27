package com.kabryxis.spiritcraft.game.a.objective.prerequisite;

import com.kabryxis.spiritcraft.game.a.objective.ObjectiveTrigger;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.block.Block;

public interface ObjectivePrerequisite {
	
	boolean canPerform(SpiritPlayer player, Block block, ObjectiveTrigger trigger);
	
}
