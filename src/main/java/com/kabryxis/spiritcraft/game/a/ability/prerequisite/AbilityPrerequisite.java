package com.kabryxis.spiritcraft.game.a.ability.prerequisite;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.game.object.GameObject;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public interface AbilityPrerequisite extends GameObject {
	
	boolean canPerform(SpiritPlayer player, AbilityTrigger trigger);
	
}
