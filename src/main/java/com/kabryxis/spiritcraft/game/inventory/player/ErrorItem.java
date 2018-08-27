package com.kabryxis.spiritcraft.game.inventory.player;

import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.inventory.InteractablePlayerItem;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ErrorItem implements InteractablePlayerItem {
	
	private final ItemBuilder itemBase = ItemBuilder.newItemBuilder(Material.OBSIDIAN).name(ChatColor.GRAY + "Error Log");
	private final int messagesToDisplay = 4;
	
	private final Game game;
	
	public ErrorItem(Game game) {
		this.game = game;
	}
	
	@Override
	public boolean click(Player player, boolean right, boolean shift) {
		SpiritPlayer spiritPlayer = game.getPlayerManager().getPlayer(player);
		if(spiritPlayer.hasErrorMessages()) {
			if(shift) spiritPlayer.clearAllErrorMessages();
			else spiritPlayer.clearLastErrorMessages(right ? messagesToDisplay : 1);
		}
		return true;
	}
	
	@Override
	public ItemStack modify(Player player, ItemStack itemStack) {
		SpiritPlayer spiritPlayer = game.getPlayerManager().getPlayer(player);
		return spiritPlayer.hasErrorMessages() ? itemBase.clone().lore(spiritPlayer.getLastErrorMessages(messagesToDisplay)).build() : null;
	}
	
}
