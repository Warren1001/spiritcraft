package com.kabryxis.spiritcraft.game.a.world.sound;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;

public class SoundCause {
	
	private final Location targetLoc;
	private final SpiritPlayer targetPlayer;
	private final SpiritPlayer owner;
	
	public SoundCause(Location loc) {
		this(loc, null);
	}
	
	public SoundCause(SpiritPlayer targetPlayer) {
		this(targetPlayer, null);
	}
	
	public SoundCause(Location targetLoc, SpiritPlayer owner) {
		this.targetLoc = targetLoc;
		this.targetPlayer = null;
		this.owner = owner;
	}
	
	public SoundCause(SpiritPlayer targetPlayer, SpiritPlayer owner) {
		this.targetPlayer = targetPlayer;
		this.targetLoc = null;
		this.owner = owner;
	}
	
	public Location getTargetLocation() {
		return targetLoc;
	}
	
	public SpiritPlayer getTargetPlayer() {
		return targetPlayer;
	}
	
	public boolean hasOwner() {
		return owner != null;
	}
	
	public SpiritPlayer getOwner() {
		return owner;
	}
	
	public void playSound(Sound sound, float volume, float pitch) {
		if(targetLoc != null) targetLoc.getWorld().playSound(targetLoc, sound, volume, pitch);
		else if(targetPlayer != null) targetPlayer.playSound(sound, volume, pitch);
		else throw new IllegalArgumentException("Both targetLoc and targetPlayer are null");
	}
	
}
