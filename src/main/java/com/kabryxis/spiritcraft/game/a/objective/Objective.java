package com.kabryxis.spiritcraft.game.a.objective;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.objective.action.ObjectiveAction;
import com.kabryxis.spiritcraft.game.a.objective.prerequisite.ObjectivePrerequisite;
import com.kabryxis.spiritcraft.game.a.world.ArenaData;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Objective {
	
	private final Map<ObjectiveTrigger, List<ObjectiveAction>> objectiveActions = new EnumMap<>(ObjectiveTrigger.class);
	private final Map<ObjectiveTrigger, List<ObjectivePrerequisite>> objectivePrerequisites = new EnumMap<>(ObjectiveTrigger.class);
	
	private final ObjectiveManager objectiveManager;
	private final Block block;
	
	public Objective(ArenaData arenaData, ObjectiveManager objectiveManager, Block block, ConfigSection section) {
		this.objectiveManager = objectiveManager;
		this.block = block;
		ConfigSection triggersChild = section.getChild("triggers");
		triggersChild.getChildren().forEach(triggerChild -> {
			ObjectiveTrigger trigger = ObjectiveTrigger.valueOf(triggerChild.get("type", String.class).toUpperCase());
			List<String> prerequisiteStrings = triggerChild.getList("requires", String.class);
			List<ObjectivePrerequisite> prerequisites = new ArrayList<>(prerequisiteStrings.size());
			prerequisiteStrings.forEach(prerequisiteString -> prerequisites.add(objectiveManager.createPrerequisite(arenaData, block, prerequisiteString)));
			objectivePrerequisites.put(trigger, prerequisites);
			List<String> actionStrings = triggerChild.getList("actions", String.class);
			List<ObjectiveAction> actions = new ArrayList<>(actionStrings.size());
			actionStrings.forEach(actionString -> actions.add(objectiveManager.createAction(arenaData, block, actionString)));
			objectiveActions.put(trigger, actions);
		});
	}
	
	public void trigger(SpiritPlayer player, ObjectiveTrigger trigger) {
		List<ObjectiveAction> actions = objectiveActions.get(trigger);
		if(actions != null && meetsPrerequisites(player, trigger)) actions.forEach(action -> action.perform(player, block, trigger));
	}
	
	public boolean meetsPrerequisites(SpiritPlayer player, ObjectiveTrigger trigger) {
		List<ObjectivePrerequisite> prerequisites = objectivePrerequisites.get(trigger);
		if(prerequisites != null) {
			for(ObjectivePrerequisite prerequisite : prerequisites) {
				if(!prerequisite.canPerform(player, block, trigger)) return false;
			}
		}
		return true;
	}
	
}
