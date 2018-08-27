package com.kabryxis.spiritcraft.game.a.ability;

import com.kabryxis.kabutils.Worker;
import com.kabryxis.kabutils.data.file.Files;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityActionCreator;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.AbilityPrerequisite;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.AbilityPrerequisiteCreator;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class AbilityManager {
	
	private final Map<String, AbilityActionCreator> actionCreators = new HashMap<>();
	private final Map<String, AbilityPrerequisiteCreator> prerequisiteCreators = new HashMap<>();
	private final Map<String, Ability> abilities = new HashMap<>();
	private final Map<String, AbilityAction> cachedActions = new HashMap<>();
	private final Map<String, AbilityPrerequisite> cachedPrerequisites = new HashMap<>();
	private final Set<Worker> abilityRequests = new HashSet<>();
	
	private final Game game;
	private final File folder;
	
	private boolean finishedConstructing = false;
	
	public AbilityManager(Game game, File folder) {
		this.game = game;
		this.folder = folder;
		folder.mkdirs();
	}
	
	public Game getGame() {
		return game;
	}
	
	public void loadAbilities() {
		finishedConstructing = false;
		abilities.clear();
		cachedActions.clear();
		cachedPrerequisites.clear();
		Files.forEachFileWithEnding(folder, ".yml", file -> {
			Config config = new Config(file);
			config.loadSync();
			createAbility(config);
		});
		finishedConstructing = true;
		abilityRequests.forEach(Worker::work);
	}
	
	public Ability createAbility(ConfigSection section) {
		return abilities.computeIfAbsent(section.getName(), name -> new Ability(this, section));
	}
	
	public void requestAbilitiesFromCommand(String originCommand, String abilityCommands, boolean requiresThrown, Consumer<? super AbilityCaller> action) {
		if(abilityCommands.contains("||")) {
			String[] abilityCommandsArray = abilityCommands.split(Pattern.quote("||"));
			for(String abilityCommand : abilityCommandsArray) {
				requestAbilityFromCommand(originCommand, abilityCommand, requiresThrown, action);
			}
		}
		else requestAbilityFromCommand(originCommand, abilityCommands, requiresThrown, action);
	}
	
	public void requestAbilityFromCommand(String originCommand, String abilityCommand, boolean requiresThrown, Consumer<? super AbilityCaller> action) {
		if(finishedConstructing) requestAbilityFromCommand0(originCommand, abilityCommand, requiresThrown, action);
		else abilityRequests.add(() -> requestAbilityFromCommand0(originCommand, abilityCommand, requiresThrown, action));
	}
	
	private void requestAbilityFromCommand0(String originCommand, String abilityCommand, boolean requiresThrown, Consumer<? super AbilityCaller> action) {
		AbilityCaller abilityCaller = getAbility(abilityCommand);
		if(abilityCaller == null) abilityCaller = createAction(abilityCommand.replace(',', ';'));
		if(abilityCaller == null) {
			System.out.println("The command '" + originCommand + "' requested the ability '" + abilityCommand + "' but the ability does not exist.");
			abilityCaller = new NullAbilityCaller(originCommand, abilityCommand);
		}
		if(!(abilityCaller instanceof NullAbilityCaller) && requiresThrown && !abilityCaller.hasTriggerType(TriggerType.THROW)) {
			System.out.println("The ability '" + abilityCommand + "' needs to be thrown by command '" + originCommand + "', but the ability does not support being thrown.");
		}
		action.accept(abilityCaller);
	}
	
	public Ability getAbility(String name) {
		return abilities.get(name);
	}
	
	public AbilityAction createAction(String action) {
		return cachedActions.computeIfAbsent(action, a -> {
			if(a.contains(":")) {
				String[] args = a.split(":", 2);
				String command = args[0];
				String data = args[1];
				if(data.isEmpty()) a = command;
				else return actionCreators.get(command).create(command, data);
			}
			return actionCreators.get(a).create(a, null);
		});
	}
	
	public void handle(SpiritPlayer player, AbilityTrigger trigger) {
		abilities.values().forEach(ability -> ability.triggerSafely(player, trigger));
	}
	
	public void registerActionCreators(AbilityActionCreator... creators) {
		for(AbilityActionCreator creator : creators) {
			registerActionCreator(creator);
		}
	}
	
	public void registerActionCreator(AbilityActionCreator creator) {
		creator.getHandledCommands().forEach(cmd -> actionCreators.put(cmd, creator));
	}
	
	public void registerPrerequisiteCreators(AbilityPrerequisiteCreator... creators) {
		for(AbilityPrerequisiteCreator creator : creators) {
			registerPrerequisiteCreator(creator);
		}
	}
	
	public void registerPrerequisiteCreator(AbilityPrerequisiteCreator creator) {
		creator.getHandledCommands().forEach(cmd -> prerequisiteCreators.put(cmd, creator));
	}
	
	public AbilityPrerequisite createPrerequisite(String action) {
		return cachedPrerequisites.computeIfAbsent(action, a -> {
			if(a.contains(":")) {
				String[] args = a.split(":", 2);
				String command = args[0];
				String data = args[1];
				if(data.isEmpty()) a = command;
				else return prerequisiteCreators.get(command).create(command, data);
			}
			return prerequisiteCreators.get(a).create(a, null);
		});
	}
	
}
