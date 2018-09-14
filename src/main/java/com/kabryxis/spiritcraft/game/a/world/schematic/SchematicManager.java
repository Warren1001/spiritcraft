package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.kabryxis.kabutils.random.weighted.conditional.ConditionalWeightedRandomArrayList;
import com.kabryxis.kabutils.random.weighted.conditional.ObjectPredicate;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SchematicManager {

	private final Map<String, SchematicWrapper> schematicRegistry = new HashMap<>();
	private final ConditionalWeightedRandomArrayList<ArenaSchematic> arenaSchematicRotation = new ConditionalWeightedRandomArrayList<>(2);
	
	private final File folder;
	
	public SchematicManager(File folder) {
		this.folder = folder;
		folder.mkdirs();
	}
	
	public File getFolder() {
		return folder;
	}
	
	public void register(SchematicWrapper schematic) {
		schematicRegistry.put(schematic.getName(), schematic);
	}
	
	public void addToRotation(ArenaSchematic schematic) {
		Validate.isTrue(schematic.equals(schematicRegistry.get(schematic.getName())), "The ArenaSchematic '" + schematic.getName() + "' has not been registered yet!");
		arenaSchematicRotation.add(schematic);
	}
	
	public void registerAndAddToRotation(ArenaSchematic schematic) {
		register(schematic);
		addToRotation(schematic);
	}
	
	public ArenaSchematic random(ObjectPredicate... predicates) {
		return arenaSchematicRotation.random(predicates);
	}
	
	public SchematicWrapper getSchematic(String name) {
		return schematicRegistry.get(name);
	}

}
