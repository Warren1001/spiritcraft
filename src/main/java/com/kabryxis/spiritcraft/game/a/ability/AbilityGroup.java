package com.kabryxis.spiritcraft.game.a.ability;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.AbilityPrerequisite;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.apache.commons.lang.Validate;

import java.util.*;
import java.util.stream.Stream;

public class AbilityGroup implements AbilityCaller {
	
	private final AbilityManager abilityManager;
	private final Ability ability;
	private final String name;
	private final Set<TriggerType> triggerTypes;
	private final List<AbilityPrerequisite> prerequisites;
	private final List<AbilityAction> actions;
	
	public AbilityGroup(AbilityManager abilityManager, Ability ability, ConfigSection section) {
		this.abilityManager = abilityManager;
		this.ability = ability;
		this.name = section.getName();
		this.prerequisites = constructPrerequisites(section.getList("requires", String.class));
		this.actions = constructActions(section.getList("actions", String.class));
		this.triggerTypes = constructTriggerTypes(section.get("type"));
	}
	
	private List<AbilityPrerequisite> constructPrerequisites(List<String> strings) {
		if(strings == null) return null;
		List<AbilityPrerequisite> prerequisites = new ArrayList<>(strings.size());
		strings.forEach(string -> prerequisites.add(abilityManager.createPrerequisite(string)));
		return prerequisites;
	}
	
	private List<AbilityAction> constructActions(List<String> strings) {
		Validate.isTrue(strings != null, "Cannot create ability '" + ability.getName() + "''s trigger '" + name + "' because it has no actions to perform.");
		List<AbilityAction> actions = new ArrayList<>(strings.size());
		strings.forEach(string -> actions.add(abilityManager.createAction(string)));
		return actions;
	}
	
	private Set<TriggerType> constructTriggerTypes(Object obj) {
		Set<TriggerType> triggerTypes;
		if(obj instanceof String) {
			triggerTypes = new HashSet<>(1);
			triggerTypes.add(TriggerType.valueOf(((String)obj).toUpperCase()));
		}
		else if(obj instanceof List) {
			List genericList = (List)obj;
			triggerTypes = new HashSet<>(genericList.size());
			for(Object o : genericList) {
				triggerTypes.add(TriggerType.valueOf(o.toString().toUpperCase()));
			}
		}
		else throw new IllegalArgumentException("Ability '" + ability.getName() + "''s trigger '" + name + "''s 'type' must be a String or List.");
		for(Iterator<TriggerType> iterator = triggerTypes.iterator(); iterator.hasNext(); ) {
			TriggerType triggerType = iterator.next();
			Stream<AbilityAction> noTriggerStream = actions.stream().filter(action -> !action.hasTriggerType(triggerType));
			if(noTriggerStream.count() > 0) {
				StringBuilder builder = new StringBuilder("The ability group '" + getName() + "' for ability '" + ability.getName() + "' has the trigger '" + triggerType.name() + "' but the following ability " +
						"actions" + " do not support that trigger: ");
				noTriggerStream.forEach(action -> {
					builder.append(action.getName());
					builder.append(",");
				});
				builder.deleteCharAt(builder.lastIndexOf(","));
				builder.append(". Removing the trigger.");
				System.out.println(builder.toString());
				iterator.remove();
			}
		}
		if(triggerTypes.size() == 0) throw new IllegalArgumentException("The ability group '" + getName() + "' for ability '" + ability.getName() + "' has no permittable triggers.");
		return triggerTypes;
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		if(meetsPrerequisites(player, trigger)) actions.forEach(action -> action.triggerSafely(player, trigger));
	}
	
	@Override
	public boolean hasTriggerType(TriggerType triggerType) {
		return triggerTypes.contains(triggerType);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public boolean meetsPrerequisites(SpiritPlayer player, AbilityTrigger trigger) {
		if(triggerTypes.contains(trigger.type)) {
			if(prerequisites != null) {
				for(AbilityPrerequisite prerequisite : prerequisites) {
					if(!prerequisite.canPerform(player, trigger)) return false;
				}
			}
			return true;
		}
		return false;
	}
	
}
