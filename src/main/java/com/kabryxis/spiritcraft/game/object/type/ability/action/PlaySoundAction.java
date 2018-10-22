package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundCause;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundPlayer;
import com.kabryxis.spiritcraft.game.object.Triggers;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;

public class PlaySoundAction extends SpiritGameObjectAction {
	
	private SoundPlayer soundPlayer;
	
	public PlaySoundAction(ConfigSection creatorData) {
		super(creatorData, "sound");
		handleSubCommand("name", true, true, data -> {
			soundPlayer = game.getSoundManager().getSoundPlayer(data);
			if(soundPlayer == null) throw new IllegalArgumentException("Could not find sound named '" + data + "'.");
		});
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		soundPlayer.playSound(new SoundCause(Triggers.getOptimalLocation(triggerData), Triggers.getOptimalTarget(triggerData)));
	}
	
}
