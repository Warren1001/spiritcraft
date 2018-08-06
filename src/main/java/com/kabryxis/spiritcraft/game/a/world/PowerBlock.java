package com.kabryxis.spiritcraft.game.a.world;

import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class PowerBlock {
	
	private final DimData dimData;
	private final Block block;
	private final Set<ItemBuilder> builders;
	
	private boolean powered = false;
	
	public PowerBlock(DimData dimData, Block block, Set<ItemBuilder> builders) {
		this.dimData = dimData;
		this.block = block;
		this.builders = builders;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public boolean isPowered() {
		return powered;
	}
	
	public void setPowered(boolean powered) {
		this.powered = powered;
	}
	
	public boolean isPowerItem(ItemStack itemStack) {
		return builders.stream().anyMatch(builder -> builder.isOf(itemStack, ItemBuilder.ItemCompareFlag.TYPE, ItemBuilder.ItemCompareFlag.DATA, ItemBuilder.ItemCompareFlag.COLORLESS_NAME));
	}
	
}
