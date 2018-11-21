package com.kabryxis.spiritcraft.game;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.kabutils.spigot.plugin.particleapi.ParticleInfo;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class GhostParticleInfo extends ParticleInfo {
	
	private final String name;

	private ItemBuilder preview;
	
	public GhostParticleInfo(ConfigSection section) {
		super(section);
		this.name = section.getName();
	}
	
	public String getName() {
		return name;
	}

	@Override
	public void load(ConfigSection section) {
		super.load(section);
		preview = section.get("item");
		offsetX = section.getDouble("offset.x", 0.125);
		offsetY = section.getDouble("offset.y", 0.5);
		offsetZ = section.getDouble("offset.z", 0.125);
	}
	
	public void display(SpiritPlayer player) {
		Location loc = player.getLocation().add(0, 1, 0);
		List<Player> otherPlayers = player.getWorld().getPlayers();
		otherPlayers.remove(player.getPlayer());
		effect.send(otherPlayers, loc, offsetX, offsetY, offsetZ, speed, particleCount, displayRadius);
		effect.send(Collections.singletonList(player.getPlayer()), loc.add(loc.getDirection().setY(0).normalize().multiply(-0.6)), offsetX, offsetY, offsetZ, speed, particleCount, displayRadius);
	}
	
}
