package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.object.schematic.Schematic;
import com.kabryxis.kabutils.data.Arrays;
import com.kabryxis.kabutils.data.file.Files;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.kabutils.random.weighted.WeightedRandomArrayList;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComplexSchematicDataContainer implements SchematicDataContainer {
	
	private final WorldManager worldManager;
	private final List<RandomArrayList<ComplexSchematicDataEntry>> dataContainerLists;
	
	public ComplexSchematicDataContainer(WorldManager worldManager, List<String> list) {
		this.worldManager = worldManager;
		dataContainerLists = new ArrayList<>(list.size());
		for(String line : list) {
			RandomArrayList<ComplexSchematicDataEntry> randomList = new WeightedRandomArrayList<>(2);
			if(line.contains(";")) Arrays.forEach(line.split(";"), s -> randomList.add(constructDataEntry(s)));
			else randomList.add(constructDataEntry(line));
			dataContainerLists.add(randomList);
		}
	}
	
	public ComplexSchematicDataEntry constructDataEntry(String line) {
		if(line.contains(",")) {
			String[] kvArgs = line.split(",");
			Schematic schematic = null;
			Config data = null;
			int weight = 1000;
			//Arena arena = null;
			for(String kvArg : kvArgs) {
				String[] commandArgs = kvArg.split(":");
				String key = commandArgs[0];
				String value = commandArgs[1];
				switch(key.toLowerCase()) {
					/*case "arena":
					case "a":
						arena = worldManager.getArenaManager().random(Strings.split(value, Pattern.quote("|")));
						break;*/
					case "name":
					case "n":
						File file = new File(worldManager.getSchematicManager().getFolder(), value);
						if(file.exists()) {
							if(value.endsWith(".yml")) data = new Config(file, true);
							else if(value.endsWith(".schematic")) schematic = worldManager.getSchematicManager().getSchematic(Files.getSimpleName(file));
							else throw new IllegalArgumentException("cannot handle extension '" + value.split("\\.")[1] + "' for multi-schematic loading");
							if(data == null && schematic == null) throw new IllegalArgumentException();
						}
						else throw new IllegalArgumentException("file at path '" + file.getPath() + "' could not be found.");
						break;
					case "weight":
					case "w":
						weight = Integer.parseInt(value);
						break;
					default:
						System.out.println("cannot handle key '" + key + "'.");
						break;
				}
			}
			return data == null ? new ComplexSchematicDataEntry(schematic, weight) : new ComplexSchematicDataEntry(worldManager, data);
		}
		else {
			File file = new File(worldManager.getSchematicManager().getFolder(), line);
			if(file.exists()) {
				if(line.endsWith(".yml")) return new ComplexSchematicDataEntry(worldManager, new Config(file, true));
				else if(line.endsWith(".schematic")) return new ComplexSchematicDataEntry(worldManager.getSchematicManager().getSchematic(Files.getSimpleName(file)));
				else throw new IllegalArgumentException("cannot handle extension '" + line.split("\\.")[1] + "' for multi-schematic loading");
			}
			else throw new IllegalArgumentException("file at path '" + file.getPath() + "' could not be found.");
		}
	}
	
	@Override
	public RoundWorldData create() {
		return new ComplexRoundWorldData(worldManager, dataContainerLists);
	}
	
}
