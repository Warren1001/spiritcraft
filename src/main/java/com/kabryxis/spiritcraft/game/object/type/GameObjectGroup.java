package com.kabryxis.spiritcraft.game.object.type;

import com.kabryxis.kabutils.data.Lists;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownEntry;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownHandler;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.object.TriggerType;
import com.kabryxis.spiritcraft.game.object.action.GameObjectAction;
import com.kabryxis.spiritcraft.game.object.prerequisite.GameObjectPrerequisite;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class GameObjectGroup implements GameObjectAction {
	
	private final GameObjectTypeManager gameObjectTypeManager;
	private final GameObjectBase base;
	
	private String name;
	private List<TriggerType> triggerTypes;
	private List<GameObjectPrerequisite> prerequisites;
	private List<GameObjectAction> actions;
	private long cooldown;
	
	public GameObjectGroup(ConfigSection creatorData) {
		this.gameObjectTypeManager = creatorData.get("objectTypeManager");
		this.base = creatorData.get("objectBase");
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Game getGame() {
		return gameObjectTypeManager.getGame();
	}
	
	public void load(ConfigSection data, ConfigSection creatorData) {
		this.name = data.getName();
		creatorData.put("objectGroup", this);
		this.prerequisites = constructPrerequisites(data.getList("requires", String.class), creatorData);
		this.actions = constructActions(data.getList("actions", String.class), creatorData);
		this.triggerTypes = constructTriggerTypes(data.get("type"));
		this.cooldown = data.getLong("cooldown", 0L);
	}
	
	private List<GameObjectPrerequisite> constructPrerequisites(List<String> strings, ConfigSection creatorData) {
		if(strings == null) return null;
		List<GameObjectPrerequisite> prerequisites = new ArrayList<>(strings.size());
		strings.forEach(string -> prerequisites.add(gameObjectTypeManager.createPrerequisite(string, creatorData)));
		return prerequisites;
	}
	
	private List<GameObjectAction> constructActions(List<String> strings, ConfigSection creatorData) {
		Validate.isTrue(strings != null, "Cannot create game object '%s''s trigger '%s' because it has no actions to perform.", base.getName(), name);
		List<GameObjectAction> actions = new ArrayList<>(strings.size());
		strings.forEach(string -> actions.add(gameObjectTypeManager.createAction(string, creatorData)));
		return actions;
	}
	
	private List<TriggerType> constructTriggerTypes(Object obj) {
		List<TriggerType> triggerTypes;
		if(obj instanceof String) triggerTypes = Collections.singletonList(TriggerType.valueOf(obj.toString().toUpperCase()));
		else if(obj instanceof List) {
			triggerTypes = Lists.convert((List<?>)obj, o -> TriggerType.valueOf(o.toString().toUpperCase()));
			/*List<?> list = (List<?>)obj;
			triggerTypes = new ArrayList<>(list.size());
			list.forEach(o -> triggerTypes.add(TriggerType.valueOf(o.toString().toUpperCase())));*/
		}
		else throw new IllegalArgumentException(String.format("Ability '%s''s trigger '%s''s 'type' must be a String or List", base.getName(), name)); // TODO
		for(Iterator<TriggerType> iterator = triggerTypes.iterator(); iterator.hasNext();) {
			TriggerType triggerType = iterator.next();
			Stream<GameObjectAction> noTriggerStream = actions.stream().filter(action -> !action.hasTriggerType(triggerType));
			if(noTriggerStream.count() > 0) {
				StringBuilder builder = new StringBuilder(
						String.format("The ability group '%s' for ability '%s' has the trigger '%s' but the following ability actions do not support that trigger: ",
								name, base.getName(), triggerType.name())); // TODO
				Iterator<GameObjectAction> it = noTriggerStream.iterator();
				it.forEachRemaining(action -> {
					builder.append(action.getName());
					if(it.hasNext()) builder.append(",");
				});
				builder.append(". Removing the trigger.");
				System.out.println(builder.toString());
				iterator.remove();
			}
		}
		if(triggerTypes.size() == 0) throw new IllegalArgumentException(String.format("The ability group '%s' for ability '%s' has no permittable triggers", name, base.getName())); // TODO
		return triggerTypes;
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		if(cooldown > 0L) {
			CooldownHandler cooldownHandler = triggerData.get("cooldownHandler");
			if(cooldownHandler != null) cooldownHandler.setCooldown(new CooldownEntry(cooldown, triggerData));
		}
		actions.forEach(action -> action.perform(triggerData));
	}
	
	@Override
	public boolean hasTriggerType(TriggerType type) {
		return triggerTypes.contains(type);
	}
	
	@Override
	public boolean canPerform(ConfigSection triggerData) {
		TriggerType type = triggerData.get("type");
		return hasTriggerType(type) && actions.stream().allMatch(action -> action.canPerform(triggerData)) &&
				(prerequisites == null || prerequisites.stream().allMatch(prerequisite -> prerequisite.canPerform(triggerData) && prerequisite.perform(triggerData)));
	}
	
}
