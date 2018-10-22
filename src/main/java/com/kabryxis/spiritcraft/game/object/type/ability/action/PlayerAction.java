package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.object.Triggers;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public class PlayerAction extends SpiritGameObjectAction {
	
	private double damage = 0.0;
	
	public PlayerAction(ConfigSection creatorData) {
		super(creatorData, "player");
		addRequiredObject("target", SpiritPlayer.class);
		handleSubCommand("damage", false, double.class, d -> damage = d);
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		execute(Triggers.getOptimalTarget(triggerData));
	}
	
	private void execute(SpiritPlayer player) {
		if(damage > 0.0) player.damage(damage);
	}
	
}
