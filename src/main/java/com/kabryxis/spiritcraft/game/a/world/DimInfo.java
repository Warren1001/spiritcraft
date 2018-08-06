package com.kabryxis.spiritcraft.game.a.world;

import com.sk89q.worldedit.EditSession;
import org.bukkit.Location;

public class DimInfo {
	
	private final WorldManager worldManager;
	private final Location location;
	private final EditSession editSession;
	
	public DimInfo(WorldManager worldManager, Location location) {
		this.worldManager = worldManager;
		this.location = location;
		this.editSession = worldManager.getEditSession(location.getWorld());
	}
	
	public Location getLocation() {
		return location;
	}
	
	public EditSession getEditSession() {
		return editSession;
	}
	
}
