package com.kabryxis.spiritcraft.game.inventory.custom;

import com.kabryxis.kabutils.spigot.version.wrapper.WrapperFactory;
import org.bukkit.entity.Player;

public interface WrappedInventory {
	
	static WrappedInventory wrap(Player player, ItemStackTracker tracker) {
		return WrapperFactory.getSupplier(WrappedInventory.class, Player.class, ItemStackTracker.class).apply(player, tracker);
	}
	
}
