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
		registerSubCommandHandler("type", false, true, data -> type = Material.getMaterial(data.toUpperCase()));
		registerSubCommandHandler("data", false, true, data -> d = (byte)Integer.parseInt(data));
	}
	
	@Override
	public boolean canPerform(SpiritPlayer player, AbilityTrigger trigger) {
		return trigger.hasBlock() && (type == null || (trigger.getBlock().getType() == type && trigger.getBlock().getData() == d));
	}
	
}
