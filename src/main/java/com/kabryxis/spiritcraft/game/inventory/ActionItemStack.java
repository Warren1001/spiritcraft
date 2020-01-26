package com.kabryxis.spiritcraft.game.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public abstract class ActionItemStack extends ItemStack {
	
	private final Consumer<Player> action;

	public ActionItemStack(Consumer<Player> action) {
		this.action = action;
	}
	
	public void clicked(Player player) {
		action.accept(player);
	}

}
