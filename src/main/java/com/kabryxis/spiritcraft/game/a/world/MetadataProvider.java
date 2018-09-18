package com.kabryxis.spiritcraft.game.a.world;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

public class MetadataProvider {
	
	private final Plugin plugin;
	
	public MetadataProvider(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void addEmptyMetadata(Metadatable metadatable, String key) {
		addMetadata(metadatable, key, new Object());
	}
	
	public void addMetadata(Metadatable metadatable, String key, Object value) {
		metadatable.setMetadata(key, new FixedMetadataValue(plugin, value));
	}
	
	public void removeMetadata(Metadatable metadatable, String key) {
		metadatable.removeMetadata(key, plugin);
	}
	
	public <T> T getMetadataValue(Metadatable metadatable, String key, Class<T> clazz) {
		MetadataValue metadataValue = metadatable.getMetadata(key).stream().filter(metadata -> metadata.getOwningPlugin() == plugin).findFirst().orElse(null);
		if(metadataValue != null) {
			Object value = metadataValue.value();
			if(clazz.isInstance(value)) return clazz.cast(value);
		}
		return null;
	}
	
}
