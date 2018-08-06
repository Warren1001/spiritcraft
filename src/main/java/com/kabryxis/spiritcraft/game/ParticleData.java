package com.kabryxis.spiritcraft.game;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import org.inventivetalent.particle.ParticleEffect;

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
		effect = ParticleEffect.valueOf(section.get("effect", String.class).toUpperCase());
		preview = ItemBuilder.newItemBuilder(section.getChild("item"));
		offsetX = section.get("offset.x", Double.class, 0.12);
		offsetY = section.get("offset.y", Double.class, 0.66);
		offsetZ = section.get("offset.z", Double.class, 0.12);
		speed = section.get("speed", Double.class, 0.0);
		particleCount = section.get("particle-count", Integer.class);
		displayRadius = section.get("display-radius", Integer.class, 16);
	}
	
	public void display(SpiritPlayer player) { // TODO get better list of players to send particle display to
		effect.send(player.getWorld().getPlayers(), player.getLocation().add(0.0, 0.75, 0.0), offsetX, offsetY, offsetZ, speed, particleCount, displayRadius);
	}
	
}
