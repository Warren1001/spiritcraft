package com.kabryxis.spiritcraft.game.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class FloatingBlock {
	
	protected final Location loc;
	private final int id;
	private final byte data;
	
	public FloatingBlock(Block block) {
		loc = block.getLocation().add(0.5, 0.5, 0.5);
		id = block.getTypeId();
		data = block.getData();
	}
	
	public void start() {
		loc.getBlock().setType(Material.AIR);
	}
	
	public void end() {
		loc.getBlock().setTypeIdAndData(id, data, false);
	}
	
}
