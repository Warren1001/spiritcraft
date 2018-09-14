package com.kabryxis.spiritcraft.deprecated;

import com.kabryxis.kabutils.data.Data;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.spigot.world.schematic.BlockSelection;
import com.kabryxis.kabutils.spigot.world.schematic.SchematicEntry;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public class SchematicLoader {
	
	private final static Charset CHARSET = StandardCharsets.UTF_8;
	private final static String SEPERATOR = ",", LINE_SEPERATOR = ";";
	
	private final SchematicManager schematicManager;
	
	public SchematicLoader(SchematicManager schematicManager) {
		this.schematicManager = schematicManager;
	}
	
	public void load(File file, Config data, Consumer<Schematic> action) {
		Data.queue(() -> action.accept(loadSync(file, data)));
	}
	
	public Schematic loadSync(File file, Config data) {
		byte[] bytes;
		try {
			bytes = Files.readAllBytes(file.toPath());
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
		String[] lines = new String(bytes, CHARSET).split(LINE_SEPERATOR);
		int size = lines.length;
		Set<SchematicEntry> schematicEntries = new HashSet<>(size);
		for(String line : lines) {
			String[] split = line.split(SEPERATOR);
			int x = Integer.parseInt(split[0]);
			int y = Integer.parseInt(split[1]);
			int z = Integer.parseInt(split[2]);
			Material type = split.length > 3 ? Material.getMaterial(split[3].toUpperCase()) : Material.AIR;
			int d = split.length > 4 ? Integer.parseInt(split[4]) : 0;
			schematicEntries.add(new SchematicEntry(x, y, z, type, d));
		}
		return new Schematic(file.getName().split("\\.")[0], file, schematicEntries, data);
	}
	
	public Schematic loadSync(String name, BlockSelection selection, Config data) {
		selection.extreme();
		int lx = selection.getLowestX(), ly = selection.getLowestY(), lz = selection.getLowestZ();
		StringBuilder builder = new StringBuilder();
		Set<Block> blocks = selection.getBlocks();
		Set<SchematicEntry> schematicEntries = new HashSet<>(blocks.size());
		for(Iterator<Block> blockIterator = blocks.iterator(); blockIterator.hasNext();) {
			Block block = blockIterator.next();
			Material type = block.getType();
			int d = block.getData();
			SchematicEntry entry = new SchematicEntry(block.getX() - lx, block.getY() - ly, block.getZ() - lz, type, d);
			schematicEntries.add(entry);
			builder.append(entry.getX());
			builder.append(SEPERATOR);
			builder.append(entry.getY());
			builder.append(SEPERATOR);
			builder.append(entry.getZ());
			if(type != Material.AIR) {
				builder.append(SEPERATOR);
				builder.append(type.toString().toLowerCase());
				if(d != 0) {
					builder.append(SEPERATOR);
					builder.append(d);
				}
			}
			if(blockIterator.hasNext()) builder.append(LINE_SEPERATOR);
		}
		File file = new File(schematicManager.getFolder(), name + ".sch");
		Data.write(file.toPath(), builder.toString().getBytes(CHARSET));
		return new Schematic(name, file, schematicEntries, data);
	}
	
}
