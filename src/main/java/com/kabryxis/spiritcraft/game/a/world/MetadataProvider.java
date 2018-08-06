package com.kabryxis.spiritcraft.game.a.world;

import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;

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
		List<MetadataValue> metadatas = block.getMetadata(key);
		for(MetadataValue metadata : metadatas) {
			if(metadata.getOwningPlugin() == plugin) {
				Object value = metadata.value();
				if(clazz.isInstance(value)) return clazz.cast(value);
				return null;
			}
		}
		return null;
	}
	
}
