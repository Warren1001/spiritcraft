package com.kabryxis.spiritcraft.game.a.ability;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.AbilityPrerequisite;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownEntry;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.apache.commons.lang3.Validate;

import java.util.*;
import java.util.stream.Stream;

public class AbilityGroup implements AbilityCaller {
	
	private final AbilityManager abilityManager;
	private final Ability ability;
	private final String name;
	private final Set<TriggerType> triggerTypes;
	private final List<AbilityPrerequisite> prerequisites;
	private final List<AbilityAction> actions;
	private final long cooldown;
	
	public AbilityGroup(AbilityManager abilityManager, Ability ability, ConfigSection section) {
		this.abilityManager = abilityManager;
		this.ability = ability;
		this.name = section.getName();
		this.prerequisites = constructPrerequisites(section.getList("requires", String.class));
		this.actions = constructActions(section.getList("actions", String.class));
		this.triggerTypes = constructTriggerTypes(section.get("type"));
		this.cooldown = section.get("cooldown", long.class, 0L);
	}
	
	private List<AbilityPrerequisite> constructPrerequisites(List<String> strings) {
		if(strings == null) return null;
		List<AbilityPrerequisite> prerequisites = new ArrayList<>(strings.size());
		strings.forEach(string -> prerequisites.add(abilityManager.createPrerequisite(string)));
		return prerequisites;
	}
	
	private List<AbilityAction> constructActions(List<String> strings) {
		Validate.isTrue(strings != null, "Cannot create ability '%s''s trigger '%s' because it has no actions to perform.", ability.getName(), name);
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
		else throw new IllegalArgumentException(String.format("Ability '%s''s trigger '%s''s 'type' must be a String or List", ability.getName(), name));
		for(Iterator<TriggerType> iterator = triggerTypes.iterator(); iterator.hasNext(); ) {
			TriggerType triggerType = iterator.next();
			Stream<AbilityAction> noTriggerStream = actions.stream().filter(action -> !action.hasTriggerType(triggerType));
			if(noTriggerStream.count() > 0) {
				StringBuilder builder = new StringBuilder(
						String.format("The ability group '%s' for ability '%s' has the trigger '%s' but the following ability actions do not support that trigger: ",
								name, ability.getName(), triggerType.name()));
				Iterator<AbilityAction> it = noTriggerStream.iterator();
				it.forEachRemaining(action -> {
					builder.append(action.getName());
					if(it.hasNext()) builder.append(",");
				});
				//builder.deleteCharAt(builder.lastIndexOf(","));
				builder.append(". Removing the trigger.");
				System.out.println(builder.toString());
				iterator.remove();
			}
		}
		if(triggerTypes.size() == 0) throw new IllegalArgumentException(String.format("The ability group '%s' for ability '%s' has no permittable triggers", name, ability.getName()));
		return triggerTypes;
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		if(meetsPrerequisites(player, trigger)) {
			if(cooldown > 0L) trigger.cooldownHandler.setCooldown(new CooldownEntry(cooldown, trigger));
			actions.forEach(action -> action.trigger(player, trigger));
		}
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
		return triggerTypes.contains(trigger.type) && (prerequisites == null || prerequisites.stream().allMatch(prerequisite -> prerequisite.canPerform(player, trigger)));
	}
	
}
