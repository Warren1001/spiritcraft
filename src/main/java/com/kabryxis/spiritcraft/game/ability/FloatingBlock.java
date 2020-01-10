package com.kabryxis.spiritcraft.game.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class FloatingBlock {
	
	protected final Location loc;
	private final Material type;
	
	public FloatingBlock(Block block) {
		loc = block.getLocation().add(0.5, 0.5, 0.5);
		type = block.getType();
	}
	
	public void start() {
		loc.getBlock().setType(Material.AIR);
	}
	
	public void end() {
		loc.getBlock().setType(type, false);
	}
	
}
