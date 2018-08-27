package com.kabryxis.spiritcraft.game.a.ability.prerequisite.impl;

import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.kabutils.string.Strings;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.AbilityParsingAbilityPrerequisite;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class HandItemPrerequisite extends AbilityParsingAbilityPrerequisite {
	
	private final ItemBuilder builder = ItemBuilder.newItemBuilder();
	private final Set<ItemBuilder.ItemCompareFlag> flags = new HashSet<>();
	
	public HandItemPrerequisite() {
		super("hand");
		registerSubCommandHandler("type", false, true, data -> {
			builder.type(Material.getMaterial(data.toUpperCase()));
			flags.add(ItemBuilder.ItemCompareFlag.TYPE);
		});
		registerSubCommandHandler("data", false, true, data -> {
			builder.data((byte)Integer.parseInt(data));
			flags.add(ItemBuilder.ItemCompareFlag.DATA);
		});
		registerSubCommandHandler("name", false, true, data -> {
			builder.name(ChatColor.translateAlternateColorCodes('&', data));
			flags.add(Strings.contains(builder.name(), ChatColor.COLOR_CHAR) ? ItemBuilder.ItemCompareFlag.NAME : ItemBuilder.ItemCompareFlag.COLORLESS_NAME);
		});
	}
	
	@Override
	public boolean canPerform(SpiritPlayer player, AbilityTrigger trigger) {
		return Items.exists(player.getInventory().getItemInHand()) && (flags.isEmpty() || builder.isOf(player.getInventory().getItemInHand(), flags.toArray(new ItemBuilder.ItemCompareFlag[0])));
	}
	
}
