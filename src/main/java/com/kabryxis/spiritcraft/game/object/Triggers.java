package com.kabryxis.spiritcraft.game.object;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Triggers {
	
	private static final List<Function<ConfigSection, Location>> optimalLocationList = new ArrayList<>();
	private static final List<Function<ConfigSection, SpiritPlayer>> optimalTargetList = new ArrayList<>();
	
	static {
		optimalLocationList.add(data -> data.get("customLoc"));
		optimalLocationList.add(data -> {
			Item item = data.get("item");
			return item == null ? null : item.getLocation();
		});
		optimalLocationList.add(data -> {
			TriggerType type = data.get("type");
			Block block = data.get("block");
			return type == TriggerType.LOOKING && block != null ? block.getLocation() : null;
		});
		optimalLocationList.add(data -> {
			SpiritPlayer target = data.get("target");
			return target == null ? null : target.getLocation();
		});
		optimalLocationList.add(data -> {
			SpiritPlayer triggerer = data.get("triggerer");
			return triggerer == null ? null : triggerer.getLocation();
		});
		optimalTargetList.add(data -> data.get("target"));
		optimalTargetList.add(data -> data.get("triggerer"));
	}
	
	public static void addOptimalLocation(int priority, Function<ConfigSection, Location> action) {
		optimalLocationList.add(priority, action);
	}
	
	public static Location getOptimalLocation(ConfigSection triggerData, Location def) {
		for(Function<ConfigSection, Location> action : optimalLocationList) {
			Location loc = action.apply(triggerData);
			if(loc != null) return loc;
		}
		return def;
	}
	
	public static Location getOptimalLocation(ConfigSection triggerData) {
		return getOptimalLocation(triggerData, null);
	}
	
	public static void addOptimalTarget(int priority, Function<ConfigSection, SpiritPlayer> action) {
		optimalTargetList.add(priority, action);
	}
	
	public static SpiritPlayer getOptimalTarget(ConfigSection triggerData) {
		for(Function<ConfigSection, SpiritPlayer> action : optimalTargetList) {
			SpiritPlayer target = action.apply(triggerData);
			if(target != null) return target;
		}
		return null;
	}
	
}
