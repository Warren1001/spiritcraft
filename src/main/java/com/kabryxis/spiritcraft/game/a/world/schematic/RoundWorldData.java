package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.kabryxis.spiritcraft.game.a.world.Arena;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;

public interface RoundWorldData {
	
	Arena getArena();
	
	void load();
	
	void relight();
	
	void unload();
	
	Location getRandomGhostSpawn();
	
	Location getRandomHunterSpawn();
	
	Location toLocation(Vector pos);
	
	Region getTotalRegion();
	
}
