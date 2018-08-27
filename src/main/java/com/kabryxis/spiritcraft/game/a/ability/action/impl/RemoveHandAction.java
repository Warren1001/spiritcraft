package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoveHandAction extends AbstractSpiritAbilityAction {
	
	private int amountToRemove = 1;
	private long duration = 0L;
	
	public RemoveHandAction() {
		super("remove_hand", TriggerType.values());
		registerSubCommandHandler("amount", false, true, data -> amountToRemove = Integer.parseInt(data));
		registerSubCommandHandler("duration", false, true, data -> duration = Long.parseLong(data));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		ItemStack itemStack = trigger.getHand();
		int amountToRemove = Math.min(itemStack.getAmount(), this.amountToRemove);
		int amount = itemStack.getAmount() - amountToRemove;
		if(amount <= 0) player.getInventory().setItemInHand(null);
		else itemStack.setAmount(amount);
		if(duration > 0L) {
			int slot = player.getInventory().getHeldItemSlot();
			ItemStack removed = itemStack.clone();
			removed.setAmount(amountToRemove);
			new BukkitRunnable() {
				
				@Override
				public void run() {
					ItemStack itemStack = player.getInventory().getItem(slot);
					if(itemStack.getType() != removed.getType()) player.getInventory().addItem(removed);
					else itemStack.setAmount(itemStack.getAmount() + removed.getAmount());
				}
				
			}.runTaskLater(player.getGame().getPlugin(), duration);
		}
	}
	
}
