package com.kabryxis.spiritcraft.game.inventory.custom.impl;

import com.kabryxis.spiritcraft.game.inventory.custom.ItemStackTracker;
import com.kabryxis.spiritcraft.game.inventory.custom.WrappedInventory;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;

public class WrappedInventoryv1_13_R2 extends PlayerInventory implements WrappedInventory {
	
	private final ItemStackTracker tracker;
	
	public WrappedInventoryv1_13_R2(Player player, ItemStackTracker tracker) {
		super(((CraftHumanEntity)player).getHandle());
		CraftHumanEntity craftHumanEntity = ((CraftHumanEntity)player);
		EntityHuman entityHuman = craftHumanEntity.getHandle();
		for (int i = 0; i < items.size(); i++) {
			items.set(i, entityHuman.inventory.items.get(i));
		}
		for (int i = 0; i < armor.size(); i++) {
			armor.set(i, entityHuman.inventory.armor.get(i));
		}
		for (int i = 0; i < extraSlots.size(); i++) {
			extraSlots.set(i, entityHuman.inventory.extraSlots.get(i));
		}
		this.itemInHandIndex = entityHuman.inventory.itemInHandIndex;
		this.transaction = entityHuman.inventory.transaction;
		entityHuman.inventory = this;
		entityHuman.defaultContainer = new ContainerPlayer(entityHuman.inventory, !entityHuman.world.isClientSide, entityHuman);
		entityHuman.activeContainer = entityHuman.defaultContainer;
		craftHumanEntity.setHandle(entityHuman);
		this.tracker = tracker;
	}
	
	public void track(ItemStack item) {
		if(item != null && Item.getId(item.getItem()) != 0) tracker.track(CraftItemStack.asCraftMirror(item));
	}
	
	public void untrack(ItemStack item) {
		if(item != null && Item.getId(item.getItem()) != 0) tracker.untrack(CraftItemStack.asCraftMirror(item));
	}
	
	public void reset() {
		tracker.untrackAll();
		items.forEach(this::track);
		armor.forEach(this::track);
		extraSlots.forEach(this::track);
	}
	
	@Override
	public void setItem(int index, ItemStack item) {
		untrack(getItem(index));
		super.setItem(index, item);
		track(item);
	}
	
	@Override
	public boolean pickup(ItemStack item) {
		if(item != null && item.getCount() != 0 && item.getItem() != null) {
			try {
				int i;
				if(item.f()) {
					i = getFirstEmptySlotIndex();
					if(i >= 0) {
						untrack(items.get(i));
						ItemStack cloned = item.cloneItemStack();
						items.set(i, cloned);
						cloned.d(5);
						cloned.setCount(0);
						track(cloned);
						return true;
					}
					else if(player.abilities.canInstantlyBuild) {
						item.setCount(0);
						return true;
					}
					return false;
				}
				else {
					do {
						i = item.getCount();
						item.setCount(attemptToAdd(item));
					} while(item.getCount() > 0 && item.getCount() < i);
					if(item.getCount() == i && player.abilities.canInstantlyBuild) {
						item.setCount(0);
						return true;
					}
					return item.getCount() < i;
				}
			} catch(Throwable throwable) {
				CrashReport crashreport = CrashReport.a(throwable, "Adding item to inventory");
				CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Item being added");
				crashreportsystemdetails.a("Item ID", Item.getId(item.getItem()));
				crashreportsystemdetails.a("Item data", item.getDamage());
				crashreportsystemdetails.a("Item name", item.getName()::getString);
				throw new ReportedException(crashreport);
			}
		}
		return false;
	}
	
	@Override
	public ItemStack splitStack(int i, int j) {
		NonNullList<ItemStack> aitemstack = this.items;
		if (i >= this.items.size()) {
			aitemstack = this.armor;
			i -= this.items.size();
		}
		
		ItemStack item = aitemstack.get(i);
		if (!item.isEmpty()) {
			if (item.getCount() <= j) {
				tracker.untrack(CraftItemStack.asCraftMirror(item));
				aitemstack.remove(i);
				return item;
			} else {
				ItemStack itemstack = item.cloneAndSubtract(j);
				if (item.getCount() == 0) {
					untrack(itemstack);
					aitemstack.remove(i);
				}
				
				return itemstack;
			}
		} else {
			return null;
		}
	}
	
	@Override
	public ItemStack splitWithoutUpdate(int i) {
		NonNullList<ItemStack> aitemstack = this.items;
		if (i >= this.items.size()) {
			aitemstack = this.armor;
			i -= this.items.size();
		}
		
		ItemStack itemstack = aitemstack.get(i);
		if (!itemstack.isEmpty()) {
			untrack(itemstack);
			aitemstack.remove(i);
			return itemstack;
		} else {
			return null;
		}
	}
	
	@Override
	public void a(PlayerInventory inventory) {
		super.a(inventory);
		reset();
	}
	
	public int firstPartial(ItemStack item) {
		for(int i = 0; i < items.size(); i++) {
			ItemStack itema = items.get(i);
			if(!itema.isEmpty() && itema.getItem() == item.getItem() && itema.isStackable() && itema.getCount() < itema.getMaxStackSize() &&
					itema.getCount() < getMaxStackSize() && ItemStack.equals(itema, item)) {
				return i;
			}
		}
		return -1;
	}
	
	public int attemptToAdd(ItemStack itemStack) {
		int i = itemStack.getCount();
		int slot = firstPartial(itemStack);
		if(slot < 0) slot = getFirstEmptySlotIndex();
		if(slot >= 0) {
			ItemStack item = items.get(slot);
			if(item.isEmpty()) {
				item = itemStack.cloneItemStack();
				item.setCount(0);
				items.set(slot, item);
				track(item);
			}
			int k = i;
			if(i > item.getMaxStackSize() - item.getCount()) k = item.getMaxStackSize() - item.getCount();
			if(k > getMaxStackSize() - item.getCount()) k = getMaxStackSize() - item.getCount();
			if(k != 0) {
				i -= k;
				item.setCount(item.getCount() + k);
				item.d(5);
				return i;
			}
			return i;
		}
		return i;
	}
	
}
