package com.kabryxis.spiritcraft.game.a.world;

import com.kabryxis.kabutils.data.MathHelp;
import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.spiritcraft.game.Schematic;
import com.kabryxis.spiritcraft.game.a.event.PlayerChangedDimEvent;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

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
	
	public void teleportToOtherDim(Entity entity) {
		Location loc = entity.getLocation().clone().add(0, 0.75, 0);
		String currWorldName = loc.getWorld().getName();
		World normalWorld = arena.getNormalDimInfo().getLocation().getWorld();
		World spiritWorld = arena.getSpiritDimInfo().getLocation().getWorld();
		PlayerChangedDimEvent.DimType newDimType;
		if(currWorldName.equals(normalWorld.getName())) {
			loc.setWorld(spiritWorld);
			newDimType = PlayerChangedDimEvent.DimType.SPIRIT;
		}
		else if(currWorldName.equals(spiritWorld.getName())) {
			loc.setWorld(normalWorld);
			newDimType = PlayerChangedDimEvent.DimType.NORMAL;
		}
		else throw new IllegalStateException(entity + " could not be found in a game world when trying to teleport between worlds.");
		Runnable run = () -> {
			entity.teleport(loc);
			double movX = new Random().nextInt(10) / 10.0 + 0.1;
			if(new Random().nextBoolean()) movX *= -1;
			double movY = 0.5;
			double movZ = 0.8 - Math.abs(movX);
			if(new Random().nextBoolean()) movZ *= -1;
			entity.setVelocity(new Vector(movX, movY, movZ));
		};
		if(entity instanceof Player) {
			SpiritPlayer player = game.getPlayerManager().getPlayer((Player)entity);
			game.getPlugin().getServer().getPluginManager().callEvent(new PlayerChangedDimEvent(player, newDimType));
			player.teleport(loc.clone().add(0, 300, 0));
			BukkitThreads.syncLater(run, 10L + MathHelp.floor(player.getPing() / 50.0));
		}
		else run.run();
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
