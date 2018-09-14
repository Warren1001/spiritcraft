package com.kabryxis.spiritcraft.deprecated;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.inventivetalent.particle.ParticleEffect;

import java.util.Collections;

public class SpiritDimData extends DimData {
	
	private BukkitTask particleTask;
	
	public SpiritDimData(ArenaData arenaData, Schematic schematic, DimInfo dimInfo) {
		super(arenaData, schematic, dimInfo);
	}
	
	@Override
	public void loadSchematic() {
		super.loadSchematic();
		Location loc = getDimInfo().getLocation();
		particleTask = BukkitThreads.syncTimer(() -> loc.getWorld().getPlayers().forEach(player -> ParticleEffect.ENCHANTMENT_TABLE.send(Collections.singletonList(player), player.getEyeLocation(), 5.5, 4.5, 5.5,
				1.5, 125, 16)), 0L, 1L); // TODO
	}
	
	@Override
	public void eraseSchematic() {
		particleTask.cancel();
		super.eraseSchematic();
	}
	
}
