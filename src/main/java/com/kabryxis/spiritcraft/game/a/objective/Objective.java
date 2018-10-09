package com.kabryxis.spiritcraft.game.a.objective;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.objective.action.ObjectiveAction;
import com.kabryxis.spiritcraft.game.a.objective.prerequisite.ObjectivePrerequisite;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Objective {
	
	private final Map<ObjectiveTrigger, List<ObjectiveAction>> objectiveActions = new EnumMap<>(ObjectiveTrigger.class);
	private final Map<ObjectiveTrigger, List<ObjectivePrerequisite>> objectivePrerequisites = new EnumMap<>(ObjectiveTrigger.class);
	
	private final ObjectiveManager objectiveManager;
	private final Block loc;
	
	public Objective(ObjectiveManager objectiveManager, Block loc, ConfigSection section) {
		this.objectiveManager = objectiveManager;
		this.loc = loc;
		ConfigSection triggersChild = section.get("triggers", ConfigSection.class);
		triggersChild.getChildren().forEach(triggerChild -> {
			ObjectiveTrigger trigger = ObjectiveTrigger.valueOf(triggerChild.get("type", String.class).toUpperCase());
			List<String> prerequisiteStrings = triggerChild.getList("requires", String.class);
			List<ObjectivePrerequisite> prerequisites = new ArrayList<>(prerequisiteStrings.size());
			prerequisiteStrings.forEach(prerequisiteString -> prerequisites.add(objectiveManager.createPrerequisite(prerequisiteString)));
			objectivePrerequisites.put(trigger, prerequisites);
			List<String> actionStrings = triggerChild.getList("actions", String.class);
			List<ObjectiveAction> actions = new ArrayList<>(actionStrings.size());
			actionStrings.forEach(actionString -> actions.add(objectiveManager.createAction(actionString)));
			objectiveActions.put(trigger, actions);
		});
	}
	
	public Block getLocation() {
		return loc;
	}
	
	public void trigger(SpiritPlayer player, ObjectiveTrigger trigger) {
		List<ObjectiveAction> actions = objectiveActions.get(trigger);
		if(actions != null && meetsPrerequisites(player, trigger)) actions.forEach(action -> action.trigger(player, loc, trigger));
	}
	
	public boolean meetsPrerequisites(SpiritPlayer player, ObjectiveTrigger trigger) {
		List<ObjectivePrerequisite> prerequisites = objectivePrerequisites.get(trigger);
		if(prerequisites != null) {
			for(ObjectivePrerequisite prerequisite : prerequisites) {
				if(!prerequisite.canPerform(player, loc, trigger)) return false;
			}
		}
		return true;
	}
	
	public void callEvent(Event event) {
		objectiveActions.values().forEach(list -> list.forEach(action -> action.event(this, event)));
	}
	
}
