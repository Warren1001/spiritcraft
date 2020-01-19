package com.kabryxis.spiritcraft.game.object.prerequisite.impl;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.spiritcraft.game.object.SpiritGameObject;
import com.kabryxis.spiritcraft.game.object.prerequisite.GameObjectPrerequisite;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class HandPrerequisite extends SpiritGameObject implements GameObjectPrerequisite {
	
	private final ItemBuilder builder = new ItemBuilder();
	private final Set<ItemBuilder.ItemCompareFlag> flags = new HashSet<>();
	
	public HandPrerequisite(ConfigSection creatorData) {
		super(creatorData, "hand");
		handleSubCommand("type", false, Material.class, material -> {
			builder.type(material);
			flags.add(ItemBuilder.ItemCompareFlag.TYPE);
		});
		handleSubCommand("data", false, byte.class, b -> {
			builder.data(b);
			flags.add(ItemBuilder.ItemCompareFlag.DATA);
		});
		handleSubCommand("name", false, true, data -> {
			builder.name(data);
			flags.add(ItemBuilder.ItemCompareFlag.NAME);
		});
	}
	
	@Override
	public boolean canPerform(ConfigSection triggerData) {
		return triggerData.get("triggerer", SpiritPlayer.class) != null;
	}
	
	@Override
	public boolean perform(ConfigSection triggerData) {
		return flags.isEmpty() || builder.isOf(triggerData.get("triggerer", SpiritPlayer.class).getInventory().getItemInMainHand(), flags);
	}
	
}
