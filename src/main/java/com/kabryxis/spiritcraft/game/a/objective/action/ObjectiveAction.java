package com.kabryxis.spiritcraft.game.a.objective.action;

import com.kabryxis.spiritcraft.game.a.game.object.GameObject;
import com.kabryxis.spiritcraft.game.a.objective.Objective;
import com.kabryxis.spiritcraft.game.a.objective.ObjectiveTrigger;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

public interface ObjectiveAction extends GameObject {
	
	void trigger(SpiritPlayer player, Block loc, ObjectiveTrigger trigger);
	
	void event(Objective objective, Event event);
	
}
