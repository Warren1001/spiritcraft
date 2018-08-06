package com.kabryxis.spiritcraft.game.a.world;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.spiritcraft.game.Schematic;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.inventivetalent.particle.ParticleEffect;

import java.util.Collections;
import java.util.List;

public class SpiritDimData extends DimData {
	
	private BukkitTask particleTask;
	
	public SpiritDimData(ArenaData arenaData, Schematic schematic, DimInfo dimInfo) {
		super(arenaData, schematic, dimInfo);
	}
	
	@Override
	public void loadSchematic() {
		super.loadSchematic();
		Location loc = getDimInfo().getLocation();
		particleTask = BukkitThreads.syncTimer(() -> {
			List<Player> players = loc.getWorld().getPlayers();
			players.forEach(player -> ParticleEffect.ENCHANTMENT_TABLE.send(Collections.singletonList(player), player.getEyeLocation(), 5.5, 4.5, 5.5, 1.5, 125, 16));
		}, 0L, 1L);
	}
	
	@Override
	public void eraseSchematic() {
		particleTask.cancel();
		super.eraseSchematic();
	}
	
}
