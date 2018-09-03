package com.kabryxis.spiritcraft.game.a.world;

import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class MetadataProvider {
	
	private final Plugin plugin;
	
	public MetadataProvider(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void addEmptyMetadata(Block block, String key) {
		addMetadata(block, key, new Object());
	}
	
	public void addMetadata(Block block, String key, Object value) {
		block.setMetadata(key, new FixedMetadataValue(plugin, value));
	}
	
	public void removeMetadata(Block block, String key) {
		block.removeMetadata(key, plugin);
	}
	
	public <T> T getMetadataValue(Block block, String key, Class<T> clazz) {
		MetadataValue metadataValue = block.getMetadata(key).stream().filter(metadata -> metadata.getOwningPlugin() == plugin).findFirst().orElse(null);
		if(metadataValue != null) {
			Object value = metadataValue.value();
			if(clazz.isInstance(value)) return clazz.cast(value);
		}
		return null;
	}
	
}
