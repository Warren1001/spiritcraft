package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.SpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundCause;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundPlayer;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

public class PlaySoundAction extends SpiritAbilityAction {
	
	private SoundPlayer soundPlayer;
	
	public PlaySoundAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "sound");
		handleSubCommand("name", true, true, data -> {
			soundPlayer = game.getSoundManager().getSoundPlayer(data);
			if(soundPlayer == null) throw new IllegalArgumentException("Could not find sound named '" + data + "'.");
		});
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		super.trigger(player, trigger);
		soundPlayer.playSound(new SoundCause(trigger.getOptimalLocation(player.getLocation()), player));
	}
	
}
