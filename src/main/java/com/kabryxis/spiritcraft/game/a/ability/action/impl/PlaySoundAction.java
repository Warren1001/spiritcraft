package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundCause;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundManager;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundPlayer;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public class PlaySoundAction extends AbstractSpiritAbilityAction {
	
	private SoundPlayer soundPlayer;
	
	public PlaySoundAction(SoundManager soundManager) {
		super("sound");
		getParseHandler().registerSubCommandHandler("name", true, true, data -> {
			soundPlayer = soundManager.getSoundPlayer(data);
			if(soundPlayer == null) throw new IllegalArgumentException("Could not find sound named '" + data + "'.");
		});
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		soundPlayer.playSound(new SoundCause(trigger.getOptimalLocation(player.getLocation()), player));
	}
	
}
