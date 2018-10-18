package com.kabryxis.spiritcraft.game.a.objective;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.a.objective.action.ObjectiveAction;
import com.kabryxis.spiritcraft.game.a.objective.prerequisite.ObjectivePrerequisite;
import com.sk89q.worldedit.Vector;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ObjectiveManager {
	
	private final Map<Block, Objective> cacheMap = new HashMap<>();
	
	private final Game game;
	private final GameObjectManager<ObjectiveAction> actionManager;
	private final GameObjectManager<ObjectivePrerequisite> prerequisiteManager;
	
	public ObjectiveManager(Game game) {
		this.game = game;
		this.actionManager = new GameObjectManager<>(game, ObjectiveAction.class);
		this.prerequisiteManager = new GameObjectManager<>(game, ObjectivePrerequisite.class);
	}
	
	public void loadObjectives(ConfigSection section) {
		section.getChildren().forEach(child -> {
			Block loc = game.getCurrentArenaData().toLocation(child.getCustom("location", Vector.class)).getBlock();
			cacheMap.put(loc, new Objective(this, loc, child));
		});
	}
	
	public void clear() {
		cacheMap.clear();
	}
	
	public Objective getObjective(Block loc) {
		return cacheMap.get(loc);
	}
	
	public void callEvent(Event event) {
		cacheMap.values().forEach(obj -> obj.callEvent(event));
	}
	
	public void registerActionCreator(String name, Function<GameObjectManager<ObjectiveAction>, ObjectiveAction> creator) {
		actionManager.registerCreator(name, creator);
	}
	
	public void registerPrerequisiteCreator(String name, Function<GameObjectManager<ObjectivePrerequisite>, ObjectivePrerequisite> creator) {
		prerequisiteManager.registerCreator(name, creator);
	}
	
	public ObjectiveAction createAction(String action) {
		return actionManager.create(action);
	}
	
	public ObjectivePrerequisite createPrerequisite(String action) {
		return prerequisiteManager.create(action);
	}
	
}
