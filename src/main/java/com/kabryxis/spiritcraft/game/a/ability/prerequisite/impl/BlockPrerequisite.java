package com.kabryxis.spiritcraft.game.a.ability.prerequisite.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.AbstractSpiritAbilityPrerequisite;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.AbilityPrerequisite;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Material;

public class BlockPrerequisite extends AbstractSpiritAbilityPrerequisite {
	
	private Material type;
	private byte d;
	
	public BlockPrerequisite(GameObjectManager<AbilityPrerequisite> objectManager) {
		super(objectManager, "block");
		handleSubCommand("type", false, true, data -> type = Material.getMaterial(data.toUpperCase()));
		handleSubCommand("data", false, byte.class, b -> d = b);
	}
	
	@Override
	public boolean canPerform(SpiritPlayer player, AbilityTrigger trigger) {
		return trigger.block != null && (type == null || (trigger.block.getType() == type && trigger.block.getData() == d));
	}
	
}
