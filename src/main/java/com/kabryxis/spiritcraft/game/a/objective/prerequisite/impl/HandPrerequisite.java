package com.kabryxis.spiritcraft.game.a.objective.prerequisite.impl;

import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.spiritcraft.game.a.objective.ObjectiveTrigger;
import com.kabryxis.spiritcraft.game.a.objective.prerequisite.ObjectivePrerequisite;
import com.kabryxis.spiritcraft.game.a.objective.prerequisite.ObjectivePrerequisiteCreator;
import com.kabryxis.spiritcraft.game.a.world.ArenaData;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HandPrerequisite implements ObjectivePrerequisiteCreator, ObjectivePrerequisite {
	
	private final Map<Block, Data> dataMap = new HashMap<>();
	
	@Override
	public ObjectivePrerequisite create(ArenaData arenaData, Block location, String data) {
		String[] dataArgs = data.split(";");
		ItemBuilder builder = ItemBuilder.newItemBuilder();
		Set<ItemBuilder.ItemCompareFlag> flags = new HashSet<>();
		for(String dataArg : dataArgs) {
			String[] subAction = dataArg.split("~");
			String subData = subAction[1];
			switch(subAction[0]) {
				case "type":
					builder.type(Material.getMaterial(subData.toUpperCase()));
					flags.add(ItemBuilder.ItemCompareFlag.TYPE);
					break;
				case "amount":
					builder.amount(Integer.parseInt(subData));
					flags.add(ItemBuilder.ItemCompareFlag.AMOUNT_MIN);
					break;
				default:
					System.out.println(getClass().getSimpleName() + " does not know how to handle sub-action '" + subAction[0] + "', skipping.");
					break;
			}
		}
		dataMap.put(location, new Data(builder, flags.toArray(new ItemBuilder.ItemCompareFlag[0])));
		return this;
	}
	
	@Override
	public boolean canPerform(SpiritPlayer player, Block block, ObjectiveTrigger trigger) {
		return dataMap.get(block).canPerform(player);
	}
	
	private class Data {
		
		private final ItemBuilder builder;
		private final ItemBuilder.ItemCompareFlag[] flags;
		
		public Data(ItemBuilder builder, ItemBuilder.ItemCompareFlag... flags) {
			this.builder = builder;
			this.flags = flags;
		}
		
		public boolean canPerform(SpiritPlayer player) {
			return builder.isOf(player.getInventory().getItemInHand(), flags);
		}
		
	}
	
}
