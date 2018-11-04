package com.kabryxis.spiritcraft.game.inventory.player;

import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.inventory.InteractablePlayerItem;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class InformationItem implements InteractablePlayerItem {
	
	private final ItemBuilder itemBase = new ItemBuilder(Material.SIGN).name(ChatColor.AQUA + "Information");
	
	private final SpiritGame game;
	
	public InformationItem(SpiritGame game) {
		this.game = game;
	}
	
	@Override
	public boolean click(Player player, boolean right, boolean shift) {
		return true;
	}
	
	@Override
	public ItemStack modify(Player player, ItemStack itemStack) {
		SpiritPlayer spiritPlayer = game.getPlayerManager().getPlayer(player);
		return itemBase.clone().lore(Arrays.asList(ChatColor.GOLD + "Money: " + ChatColor.YELLOW + spiritPlayer.getCurrency(), ChatColor.GOLD + "Space: " + ChatColor.YELLOW + spiritPlayer.getItemSpace())).build();
	}
	
}
