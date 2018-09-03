package com.kabryxis.spiritcraft.game.a.ability.prerequisite.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.AbilityParsingAbilityPrerequisite;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Material;

public class BlockPrerequisite extends AbilityParsingAbilityPrerequisite {
	
	private Material type;
	private byte d;
	
	public BlockPrerequisite() {
		super("block");
		getParseHandler().registerSubCommandHandler("type", false, true, data -> type = Material.getMaterial(data.toUpperCase()));
		getParseHandler().registerSubCommandHandler("data", false, byte.class, b -> d = b);
	}
	
	@Override
	public boolean canPerform(SpiritPlayer player, AbilityTrigger trigger) {
		return trigger.block != null && (type == null || (trigger.block.getType() == type && trigger.block.getData() == d));
	}
	
}
