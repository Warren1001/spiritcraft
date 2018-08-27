package com.kabryxis.spiritcraft.game.a.objective.action;

import com.kabryxis.spiritcraft.game.a.objective.ObjectiveTrigger;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.block.Block;

public interface ObjectiveAction {
	
	void perform(SpiritPlayer player, Block location, ObjectiveTrigger trigger);
	
}
