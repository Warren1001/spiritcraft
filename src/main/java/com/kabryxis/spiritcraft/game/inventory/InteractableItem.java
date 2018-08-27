package com.kabryxis.spiritcraft.game.inventory;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface InteractableItem {
	
	boolean click(Player player, boolean right, boolean shift);
	
}
