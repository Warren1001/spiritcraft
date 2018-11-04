package com.kabryxis.spiritcraft.game.object.type.objective;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.object.action.GameObjectAction;
import com.kabryxis.spiritcraft.game.object.prerequisite.GameObjectPrerequisite;
import com.kabryxis.spiritcraft.game.object.type.GameObjectBase;
import com.kabryxis.spiritcraft.game.object.type.GameObjectTypeManager;
import com.sk89q.worldedit.Vector;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class ObjectiveManager extends GameObjectTypeManager {
	
	private final Map<Block, GameObjectBase> objectivesByBlock = new HashMap<>();
	
	public ObjectiveManager(SpiritGame game) {
		super(game, GameObjectAction.class, GameObjectPrerequisite.class);
	}
	
	public void loadObjectives(ConfigSection section) {
		section.getChildren().forEach(this::createBase);
	}
	
	public GameObjectBase getObjective(Block loc) {
		return objectivesByBlock.get(loc);
	}
	
	@Override
	public GameObjectBase createBase(ConfigSection data) {
		Block block = game.getCurrentArenaData().toLocation(data.getCustom("location", Vector.class)).getBlock();
		ConfigSection creatorData = new ConfigSection();
		creatorData.put("objectTypeManager", this);
		creatorData.put("baseData", data);
		creatorData.put("objectiveBlock", block);
		GameObjectBase base = baseCreator.apply(creatorData);
		gameObjectBases.add(base);
		objectivesByBlock.put(block, base);
		return base;
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		Block block = triggerData.get("block");
		if(block != null) {
			GameObjectBase objective = getObjective(block);
			if(objective != null && objective.canPerform(triggerData)) objective.perform(triggerData);
		}
	}
	
	public void clear() {
		objectivesByBlock.clear();
		gameObjectBases.clear();
	}
	
}
