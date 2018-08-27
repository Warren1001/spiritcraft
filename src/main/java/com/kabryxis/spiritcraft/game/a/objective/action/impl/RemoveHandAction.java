package com.kabryxis.spiritcraft.game.a.objective.action.impl;

import com.kabryxis.spiritcraft.game.a.objective.ObjectiveTrigger;
import com.kabryxis.spiritcraft.game.a.objective.action.ObjectiveAction;
import com.kabryxis.spiritcraft.game.a.objective.action.ObjectiveActionCreator;
import com.kabryxis.spiritcraft.game.a.world.DimData;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RemoveHandAction implements ObjectiveActionCreator, ObjectiveAction {
	
	private final Map<Block, Data> dataMap = new HashMap<>();
	
	@Override
	public RemoveHandAction create(DimData dimData, Block location, String data) {
		dataMap.put(location, new Data(Integer.parseInt(data)));
		return this;
	}
	
	@Override
	public void perform(SpiritPlayer player, Block location, ObjectiveTrigger trigger) {
		dataMap.get(location).perform(player);
	}
	
	private class Data {
		
		private final int amountToRemove;
		
		public Data(int amountToRemove) {
			this.amountToRemove = amountToRemove;
		}
		
		public void perform(SpiritPlayer player) {
			ItemStack item = player.getInventory().getItemInHand();
			int amount = item.getAmount() - amountToRemove;
			if(amount <= 0) player.getInventory().setItemInHand(new ItemStack(Material.AIR));
			else item.setAmount(amount);
		}
		
	}
	
}
