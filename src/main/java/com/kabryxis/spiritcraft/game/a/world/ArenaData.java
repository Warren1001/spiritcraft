package com.kabryxis.spiritcraft.game.a.world;

import com.kabryxis.spiritcraft.game.Schematic;
import com.kabryxis.spiritcraft.game.a.event.PlayerChangedDimEvent;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;

public class ArenaData {
	
	private final Game game;
	private final Arena arena;
	private final DimData normalDimData;
	private final SpiritDimData spiritDimData;
	
	public ArenaData(Game game, Arena arena, Schematic schematic) {
		this.game = game;
		this.arena = arena;
		this.normalDimData = new DimData(this, schematic, arena.getNormalDimInfo());
		this.spiritDimData = new SpiritDimData(this, game.getWorldManager().getSchematicManager().getSpirit(schematic.getName()), arena.getSpiritDimInfo());
	}
	
	public Game getGame() {
		return game;
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public DimData getNormalDimData() {
		return normalDimData;
	}
	
	public SpiritDimData getSpiritDimData() {
		return spiritDimData;
	}
	
	public void teleportToOtherDim(SpiritPlayer player) {
		Location loc = player.getLocation().clone();
		if(arena.getNormalDimInfo().getLocation().getWorld().getName().equals(loc.getWorld().getName())) {
			loc.setWorld(arena.getSpiritDimInfo().getLocation().getWorld());
			player.teleport(loc);
			player.getPlayer().getServer().getPluginManager().callEvent(new PlayerChangedDimEvent(player, PlayerChangedDimEvent.DimType.SPIRIT));
		}
		else if(arena.getSpiritDimInfo().getLocation().getWorld().getName().equals(loc.getWorld().getName())) {
			loc.setWorld(arena.getNormalDimInfo().getLocation().getWorld());
			player.teleport(loc);
			player.getPlayer().getServer().getPluginManager().callEvent(new PlayerChangedDimEvent(player, PlayerChangedDimEvent.DimType.NORMAL));
		}
		else {
			System.out.println("player is in world '" + player.getWorld().getName() + "' which is not one of the arena worlds" +
					"[normal:" + arena.getNormalDimInfo().getLocation().getWorld().getName() + ",spirit:" + arena.getSpiritDimInfo().getLocation().getWorld().getName() + "]");
		}
	}
	
	public void load() {
		arena.load();
		normalDimData.loadSchematic();
		spiritDimData.loadSchematic();
	}
	
	public void unload() {
		normalDimData.eraseSchematic();
		spiritDimData.eraseSchematic();
		arena.unload();
	}
	
}
