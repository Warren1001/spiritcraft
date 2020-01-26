package com.kabryxis.spiritcraft.game.object.action.impl;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.inventory.ItemStack;

public class RemoveHandAction extends SpiritGameObjectAction {
	
	private int amountToRemove = 1;
	private long duration = 0L;
	
	public RemoveHandAction(ConfigSection creatorData) {
		super(creatorData, "remove_hand");
		addRequiredObject("triggerer", SpiritPlayer.class);
		addRequiredObject("hand", ItemStack.class);
		handleSubCommand("amount", false, int.class, i -> amountToRemove = i);
		handleSubCommand("duration", false, long.class, l -> duration = l);
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		SpiritPlayer player = triggerData.get("triggerer");
		ItemStack itemStack = triggerData.get("hand");
		int amountToRemove = Math.min(itemStack.getAmount(), this.amountToRemove);
		int amount = itemStack.getAmount() - amountToRemove;
		if(amount <= 0) player.getInventory().setItemInMainHand(null);
		else itemStack.setAmount(amount);
		if(duration > 0L) {
			int slot = player.getInventory().getHeldItemSlot();
			ItemStack removed = itemStack.clone();
			removed.setAmount(amountToRemove);
			game.getTaskManager().start(() -> {
				ItemStack item = player.getInventory().getItem(slot);
				if(Items.exists(item)) {
					if(Items.isType(item, removed.getType())) item.setAmount(item.getAmount() + removed.getAmount());
					else player.getInventory().addItem(removed);
				}
				else player.getInventory().setItem(slot, removed);
			}, duration);
		}
	}
	
}
