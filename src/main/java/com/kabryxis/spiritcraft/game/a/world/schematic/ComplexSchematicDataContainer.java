package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.boydti.fawe.object.schematic.Schematic;
import com.kabryxis.kabutils.Pair;
import com.kabryxis.kabutils.data.Arrays;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.RandomArrayList;
import com.kabryxis.kabutils.random.weighted.Weighted;
import com.kabryxis.kabutils.random.weighted.WeightedRandomArrayList;
import com.kabryxis.kabutils.string.Strings;
import com.kabryxis.spiritcraft.game.a.world.Arena;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ComplexSchematicDataContainer implements SchematicDataContainer {
	
	public static File SCHEMATIC_FOLDER = new File("worlddata" + File.separator + "schematics");
	
	private final WorldManager worldManager;
	private final List<RandomArrayList<ComplexSchematicDataEntry>> dataContainerLists;
	
	public ComplexSchematicDataContainer(WorldManager worldManager, List<String> list) {
		this.worldManager = worldManager;
		SCHEMATIC_FOLDER.mkdirs();
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
			int weight = -1;
			Arena arena = null;
			for(String kvArg : kvArgs) {
				String[] commandArgs = kvArg.split(":");
				String key = commandArgs[0];
				String value = commandArgs[1];
				switch(key.toLowerCase()) {
					case "arena":
					case "a":
						arena = worldManager.getArenaManager().random(Strings.split(value, Pattern.quote("|")));
						break;
					case "schematic":
					case "sch":
					case "s":
						Pair<Schematic, Config> pair = getSchematic(value);
						schematic = pair.getKey();
						data = pair.getValue();
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
			if(schematic == null) throw new IllegalArgumentException("Could not find schematic");
			return data == null ?
					(weight == -1 ? new DatalessComplexSchematicDataEntry(schematic) : new DatalessComplexSchematicDataEntry(schematic, weight)) :
					(weight == -1 ? new DataComplexSchematicDataEntry(schematic, data) : new DataComplexSchematicDataEntry(schematic, data, weight));
		}
		else {
			if(line.contains(":")) {
				String[] commandArgs = line.split(":");
				String key = commandArgs[0].toLowerCase();
				String value = commandArgs[1];
				if(key.equals("schematic") || key.equals("sch") || key.equals("s")) {
					Pair<Schematic, Config> pair = getSchematic(value);
					Schematic schematic = pair.getKey();
					Config data = pair.getValue();
					return data == null ? new DatalessComplexSchematicDataEntry(schematic) : new DataComplexSchematicDataEntry(schematic, data);
				}
				else throw new IllegalArgumentException("Schematic not provided");
			}
			else {
				Pair<Schematic, Config> pair = getSchematic(line);
				Schematic schematic = pair.getKey();
				Config data = pair.getValue();
				return data == null ? new DatalessComplexSchematicDataEntry(schematic) : new DataComplexSchematicDataEntry(schematic, data);
			}
		}
	}
	
	public Pair<Schematic, Config> getSchematic(String name) {
		return null; // todo
	}
	
	@Override
	public RoundWorldData create() {
		return null;
	}
	
	public interface ComplexSchematicDataEntry extends Weighted {
		
		Schematic getSchematic();
		
	}
	
	public static class DatalessComplexSchematicDataEntry implements ComplexSchematicDataEntry {
		
		private final Schematic schematic;
		private final int weight;
		
		public DatalessComplexSchematicDataEntry(Schematic schematic, int weight) {
			this.schematic = schematic;
			this.weight = weight;
		}
		
		public DatalessComplexSchematicDataEntry(Schematic schematic) {
			this(schematic, 1000);
		}
		
		@Override
		public Schematic getSchematic() {
			return schematic;
		}
		
		@Override
		public int getWeight() {
			return weight;
		}
		
	}
	
	public static class DataComplexSchematicDataEntry implements ComplexSchematicDataEntry {
		
		private final Schematic schematic;
		private final Config data;
		private final int weight;
		
		public DataComplexSchematicDataEntry(Schematic schematic, Config data, int weight) {
			this.schematic = schematic;
			this.data = data;
			this.weight = weight;
		}
		
		public DataComplexSchematicDataEntry(Schematic schematic, Config data) {
			this(schematic, data, -1);
		}
		
		@Override
		public Schematic getSchematic() {
			return schematic;
		}
		
		public Config getData() {
			return data;
		}
		
		@Override
		public int getWeight() {
			return weight == -1 ? data.getInt("weight", 1000) : weight;
		}
		
	}
	
}
