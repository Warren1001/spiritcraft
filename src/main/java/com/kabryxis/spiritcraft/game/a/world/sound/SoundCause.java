package com.kabryxis.spiritcraft.game.a.world.sound;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;

public class SoundCause {
	
	private final Location loc;
	private final SpiritPlayer owner;
	
	public SoundCause(Location loc) {
		this(loc, null);
	}
	
	public SoundCause(Location loc, SpiritPlayer owner) {
		this.loc = loc;
		this.owner = owner;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public boolean hasOwner() {
		return owner != null;
	}
	
	public SpiritPlayer getOwner() {
		return owner;
	}
	
}
