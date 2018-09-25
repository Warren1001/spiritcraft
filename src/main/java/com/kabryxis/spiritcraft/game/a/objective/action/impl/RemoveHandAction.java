package com.kabryxis.spiritcraft.game.a.objective.action.impl;

import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.a.objective.ObjectiveTrigger;
import com.kabryxis.spiritcraft.game.a.objective.action.AbstractSpiritObjectiveAction;
import com.kabryxis.spiritcraft.game.a.objective.action.ObjectiveAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class RemoveHandAction extends AbstractSpiritObjectiveAction {
	
	private int amountToRemove = 1;
	
	public RemoveHandAction(GameObjectManager<ObjectiveAction> objectManager) {
		super(objectManager, "remove_hand");
		handleSubCommand("amount", false, int.class, data -> amountToRemove = data);
	}
	
	@Override
	public void trigger(SpiritPlayer player, Block location, ObjectiveTrigger trigger) {
		ItemStack item = player.getInventory().getItemInHand();
		int amount = item.getAmount() - amountToRemove;
		if(amount <= 0) player.getInventory().setItemInHand(new ItemStack(Material.AIR));
		else item.setAmount(amount);
	}
	
}
