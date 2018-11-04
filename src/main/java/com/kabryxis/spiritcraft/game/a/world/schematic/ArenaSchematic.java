package com.kabryxis.spiritcraft.game.a.world.schematic;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.random.weighted.Weighted;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import org.bukkit.Material;

import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ArenaSchematic extends SchematicWrapper implements Weighted {
	
	private final Config data;
	private final Set<Material> protectedBlocks = EnumSet.noneOf(Material.class);
	
	public ArenaSchematic(Config data) {
		this(new File(data.getFile().getParent(), String.format("%s.%s", data.get("sch", data.getName()), ClipboardFormat.SCHEMATIC.getExtension())), data);
	}
	
	public ArenaSchematic(File schematicFile, File dataFile) {
		this(schematicFile, new Config(dataFile, true));
	}
	
	public ArenaSchematic(File schematicFile, Config data) {
		super(schematicFile);
		this.data = data;
		List<Material> list = data.getList("protected", object -> Material.matchMaterial(object.toString()));
		if(list != null) protectedBlocks.addAll(list);
	}
	
	public Config getData() {
		return data;
	}
	
	@Override
	public int getWeight() {
		return data.getInt("weight", 1000);
	}
	
	public Set<Material> getProtectedBlocks() {
		return protectedBlocks;
	}
	
}
