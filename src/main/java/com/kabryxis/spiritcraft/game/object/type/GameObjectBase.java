package com.kabryxis.spiritcraft.game.object.type;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownEntry;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownHandler;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.object.TriggerType;
import com.kabryxis.spiritcraft.game.object.action.GameObjectAction;

import java.util.Set;
import java.util.stream.Collectors;

public class GameObjectBase implements GameObjectAction {
	
	private final GameObjectTypeManager objectTypeManager;
	
	private String name;
	private Set<GameObjectGroup> triggerGroups;
	private long cooldown;
	
	public GameObjectBase(ConfigSection creatorData) {
		this.objectTypeManager = creatorData.get("objectTypeManager");
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Game getGame() {
		return objectTypeManager.getGame();
	}
	
	public void load(ConfigSection data, ConfigSection creatorData) {
		this.name = data.getName();
		creatorData.put("objectBase", this);
		this.triggerGroups = data.get("triggers", ConfigSection.class).getChildren().stream().map(child -> {
			ConfigSection groupCreatorData = new ConfigSection(creatorData).builderPut("groupData", child);
			GameObjectGroup objectGroup = new GameObjectGroup(groupCreatorData);
			objectGroup.load(child, groupCreatorData);
			return objectGroup;
		}).collect(Collectors.toSet());
		this.cooldown = data.getLong("cooldown", 0L);
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		if(cooldown > 0L) {
			CooldownHandler cooldownHandler = triggerData.get("cooldownHandler");
			if(cooldownHandler != null) cooldownHandler.setCooldown(new CooldownEntry(cooldown, triggerData));
		}
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
