package com.kabryxis.spiritcraft.game.object.prerequisite.impl;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.object.SpiritGameObject;
import com.kabryxis.spiritcraft.game.object.prerequisite.GameObjectPrerequisite;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockPrerequisite extends SpiritGameObject implements GameObjectPrerequisite {
	
	private Material type;
	private byte d;
	
	public BlockPrerequisite(ConfigSection creatorData) {
		super(creatorData, "block");
		handleSubCommand("type", false, Material.class, material -> type = material);
		handleSubCommand("data", false, byte.class, b -> d = b);
	}
	
	@Override
	public boolean canPerform(ConfigSection triggerData) {
		return triggerData.get("block", Block.class) != null;
	}
	
	@Override
	public boolean perform(ConfigSection triggerData) {
		Block block = triggerData.get("block");
		return type == null || (block.getType() == type && block.getData() == d);
	}
	
}
