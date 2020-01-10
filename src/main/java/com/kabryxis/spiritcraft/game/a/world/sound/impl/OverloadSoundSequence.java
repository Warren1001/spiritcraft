package com.kabryxis.spiritcraft.game.a.world.sound.impl;

import com.kabryxis.kabutils.spigot.concurrent.TickingBukkitRunnable;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundCause;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundPlayer;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

public class OverloadSoundSequence implements SoundPlayer {
	
	private final SpiritGame game;
	
	private BukkitTask task;
	
	public OverloadSoundSequence(SpiritGame game) {
		this.game = game;
	}
	
	@Override
	public String getName() {
		return "overload";
	}
	
	@Override
	public void playSound(SoundCause cause) {
		task = game.getTaskManager().start(new TickingBukkitRunnable() {
			
			private final int speedInterval = 100;
			
			private int soundInterval = 30;
			private int lastTicked = 0;
			
			@Override
			public void tick(int tick) {
				System.out.println(tick);
				//System.out.println("tick:" + tick + ",lastTicked:" + lastTicked + ",-:" + (tick - lastTicked));
				if(tick != 0 && tick % speedInterval == 0) {
					soundInterval -= soundInterval <= 5 ? 1 : 5;
					//System.out.println("soundInterval: " + soundInterval);
					if(soundInterval == 2) {
						cancel();
						return;
					}
				}
				if(tick == 0 || tick - lastTicked >= soundInterval) {
					cause.playSound(Sound.BLOCK_PORTAL_TRAVEL, 10F, 10F);
					lastTicked = tick;
				}
			}
			
		}, 0L, 1L);
	}
	
}
