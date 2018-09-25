package com.kabryxis.spiritcraft.game.a.objective.prerequisite.impl;

import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.kabutils.string.Strings;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.a.objective.ObjectiveTrigger;
import com.kabryxis.spiritcraft.game.a.objective.prerequisite.AbstractSpiritObjectivePrerequisite;
import com.kabryxis.spiritcraft.game.a.objective.prerequisite.ObjectivePrerequisite;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

public class HandPrerequisite extends AbstractSpiritObjectivePrerequisite {
	
	private final ItemBuilder builder = ItemBuilder.newItemBuilder();
	private final Set<ItemBuilder.ItemCompareFlag> flags = new HashSet<>();
	
	public HandPrerequisite(GameObjectManager<ObjectivePrerequisite> objectManager) {
		super(objectManager, "hand");
		handleSubCommand("type", false, true, data -> {
			builder.type(Material.getMaterial(data.toUpperCase()));
			flags.add(ItemBuilder.ItemCompareFlag.TYPE);
		});
		handleSubCommand("data", false, byte.class, b -> {
			builder.data(b);
			flags.add(ItemBuilder.ItemCompareFlag.DATA);
		});
		handleSubCommand("name", false, true, data -> {
			builder.name(ChatColor.translateAlternateColorCodes('&', data));
			flags.add(Strings.contains(builder.name(), ChatColor.COLOR_CHAR) ? ItemBuilder.ItemCompareFlag.NAME : ItemBuilder.ItemCompareFlag.COLORLESS_NAME);
		});
	}
	
	@Override
	public boolean canPerform(SpiritPlayer player, Block block, ObjectiveTrigger trigger) {
		return Items.exists(player.getInventory().getItemInHand()) && (flags.isEmpty() || builder.isOf(player.getInventory().getItemInHand(), flags.toArray(new ItemBuilder.ItemCompareFlag[0])));
	}
	
}
