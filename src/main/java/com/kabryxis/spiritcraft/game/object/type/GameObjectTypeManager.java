package com.kabryxis.spiritcraft.game.object.type;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.object.action.GameObjectAction;
import com.kabryxis.spiritcraft.game.object.prerequisite.GameObjectPrerequisite;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class GameObjectTypeManager {
	
	protected final Set<GameObjectBase> gameObjectBases = new HashSet<>();
	
	protected final SpiritGame game;
	protected final GameObjectManager<GameObjectAction> actionManager;
	protected final GameObjectManager<GameObjectPrerequisite> prerequisiteManager;
	protected final Function<ConfigSection, GameObjectBase> baseCreator;
	
	public GameObjectTypeManager(SpiritGame game, Class<? extends GameObjectAction> actionClass, Class<? extends GameObjectPrerequisite> prerequisiteClass, Function<ConfigSection, GameObjectBase> baseCreator) {
		this.game = game;
		this.actionManager = new GameObjectManager<>(game, actionClass);
		this.prerequisiteManager = new GameObjectManager<>(game, prerequisiteClass);
		this.baseCreator = baseCreator;
	}
	
	public GameObjectTypeManager(SpiritGame game, Class<? extends GameObjectAction> actionClass, Class<? extends GameObjectPrerequisite> prerequisiteClass) {
		this(game, actionClass, prerequisiteClass, creatorData -> {
			GameObjectBase objectBase = new GameObjectBase(creatorData);
			objectBase.load(creatorData.get("baseData"), creatorData);
			return objectBase;
		});
	}
	
	public SpiritGame getGame() {
		return game;
	}
	
	public GameObjectBase createBase(ConfigSection data) {
		ConfigSection creatorData = new ConfigSection();
		creatorData.put("objectTypeManager", this);
		creatorData.put("baseData", data);
		GameObjectBase base = baseCreator.apply(creatorData);
		gameObjectBases.add(base);
		return base;
	}
	
	public void perform(ConfigSection triggerData) {
		gameObjectBases.stream().filter(base -> base.canPerform(triggerData)).forEach(base -> base.perform(triggerData));
	}
	
	public void registerActionCreator(String name, Function<ConfigSection, GameObjectAction> creator) {
		actionManager.registerCreator(name, creator);
	}
	
	public GameObjectAction createAction(String action, ConfigSection creatorData) {
		creatorData.put("objectTypeManager", this);
		return actionManager.get(action, creatorData);
	}
	
	public GameObjectAction createAction(String action) {
		return createAction(action, new ConfigSection());
	}
	
	public void registerPrerequisiteCreator(String name, Function<ConfigSection, GameObjectPrerequisite> creator) {
		prerequisiteManager.registerCreator(name, creator);
	}
	
	public GameObjectPrerequisite createPrerequisite(String action, ConfigSection creatorData) {
		creatorData.put("objectTypeManager", this);
		return prerequisiteManager.get(action, creatorData);
	}
	
	public GameObjectPrerequisite createPrerequisite(String action) {
		return createPrerequisite(action, new ConfigSection());
	}
	
}
