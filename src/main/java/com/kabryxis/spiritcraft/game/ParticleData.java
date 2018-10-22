package com.kabryxis.spiritcraft.game;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.inventivetalent.particle.ParticleEffect;

import java.util.Collections;
import java.util.List;

public class ParticleData {
	
	private final String name;
	
	private ParticleEffect effect;
	private ItemBuilder preview;
	private double offsetX, offsetY, offsetZ;
	private double speed;
	private int particleCount;
	private int displayRadius;
	
	public ParticleData(ConfigSection section) {
		this.name = section.getName();
		load(section);
	}
	
	public String getName() {
		return name;
	}
	
	public void load(ConfigSection section) {
		effect = section.getEnum("effect", ParticleEffect.class);
		preview = new ItemBuilder(section.get("item", ConfigSection.class));
		offsetX = section.getDouble("offset.x", 0.125);
		offsetY = section.getDouble("offset.y", 0.5);
		offsetZ = section.getDouble("offset.z", 0.125);
		speed = section.getDouble("speed");
		particleCount = section.getInt("particle-count");
		displayRadius = section.getInt("display-radius", 32);
	}
	
	public void display(SpiritPlayer player) {
		Location loc = player.getLocation().clone().add(0, 1, 0);
		List<Player> otherPlayers = player.getWorld().getPlayers();
		otherPlayers.remove(player.getPlayer());
		effect.send(otherPlayers, loc, offsetX, offsetY, offsetZ, speed, particleCount, displayRadius);
		effect.send(Collections.singletonList(player.getPlayer()), loc.add(loc.getDirection().setY(0).multiply(-0.4)), offsetX, offsetY, offsetZ, speed, particleCount, displayRadius);
	}
	
}
