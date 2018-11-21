package com.kabryxis.spiritcraft.game.object.type;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.object.TriggerType;
import com.kabryxis.spiritcraft.game.object.action.GameObjectAction;

import java.util.Set;
import java.util.stream.Collectors;

public class GameObjectBase implements GameObjectAction {
	
	private final GameObjectTypeManager objectTypeManager;
	
	private String name;
	private Set<GameObjectGroup> triggerGroups;
	
	public GameObjectBase(ConfigSection creatorData) {
		this.objectTypeManager = creatorData.get("objectTypeManager");
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public SpiritGame getGame() {
		return objectTypeManager.getGame();
	}
	
	public void load(ConfigSection data, ConfigSection creatorData) {
		name = data.getName();
		creatorData.put("objectBase", this);
		triggerGroups = data.get("triggers", ConfigSection.class).getChildren().stream().map(child -> {
			ConfigSection groupCreatorData = new ConfigSection(creatorData).builderPut("groupData", child);
			GameObjectGroup objectGroup = new GameObjectGroup(groupCreatorData);
			objectGroup.load(child, groupCreatorData);
			return objectGroup;
		}).collect(Collectors.toSet());
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		triggerGroups.stream().filter(group -> group.canPerform(triggerData)).forEach(group -> group.perform(triggerData));
	}
	
	@Override
	public boolean hasTriggerType(TriggerType type) {
		return triggerGroups.stream().anyMatch(group -> group.hasTriggerType(type));
	}
	
	@Override
	public boolean canPerform(ConfigSection triggerData) {
		return triggerGroups.stream().anyMatch(group -> group.canPerform(triggerData));
	}
	
}
