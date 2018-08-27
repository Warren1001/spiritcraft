package com.kabryxis.spiritcraft.game.a.objective;

import com.kabryxis.spiritcraft.game.a.objective.action.ObjectiveAction;
import com.kabryxis.spiritcraft.game.a.objective.action.ObjectiveActionCreator;
import com.kabryxis.spiritcraft.game.a.objective.prerequisite.ObjectivePrerequisite;
import com.kabryxis.spiritcraft.game.a.objective.prerequisite.ObjectivePrerequisiteCreator;
import com.kabryxis.spiritcraft.game.a.world.DimData;
import org.apache.commons.lang3.Validate;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class ObjectiveManager {
	
	private final Map<String, ObjectiveActionCreator> actionCreators = new HashMap<>();
	private final Map<String, ObjectivePrerequisiteCreator> prerequisiteCreators = new HashMap<>();
	
	public void registerActionCreators(Object... objs) {
		Validate.isTrue(objs.length % 2 == 0, "An even number of objects must be provided to have matching key and value pairs.");
		for(int i = 0; i < objs.length - 1; i += 2) {
			registerActionCreator((String)objs[i], (ObjectiveActionCreator)objs[i + 1]);
		}
	}
	
	public void registerActionCreator(String key, ObjectiveActionCreator creator) {
		actionCreators.put(key, creator);
	}
	
	public void registerPrerequisiteCreators(Object... objs) {
		Validate.isTrue(objs.length % 2 == 0, "An even number of objects must be provided to have matching key and value pairs.");
		for(int i = 0; i < objs.length - 1; i += 2) {
			registerPrerequisiteCreator((String)objs[i], (ObjectivePrerequisiteCreator)objs[i + 1]);
		}
	}
	
	public void registerPrerequisiteCreator(String key, ObjectivePrerequisiteCreator creator) {
		prerequisiteCreators.put(key, creator);
	}
	
	public ObjectiveAction createAction(DimData dimData, Block location, String action) {
		String[] args = action.split(":", 2);
		return actionCreators.get(args[0]).create(dimData, location, args[1]);
	}
	
	public ObjectivePrerequisite createPrerequisite(DimData dimData, Block location, String action) {
		String[] args = action.split(":", 2);
		return prerequisiteCreators.get(args[0]).create(dimData, location, args[1]);
	}
	
}
