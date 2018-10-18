package com.kabryxis.spiritcraft.game.a.ability;

import com.kabryxis.kabutils.Worker;
import com.kabryxis.kabutils.data.file.Files;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.AbilityPrerequisite;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class AbilityManager {
	
	private final Map<String, Ability> abilities = new HashMap<>();
	private final Set<Worker> abilityRequests = new HashSet<>();
	
	private final Game game;
	private final File folder;
	private final GameObjectManager<AbilityAction> actionManager;
	private final GameObjectManager<AbilityPrerequisite> prerequisiteManager;
	
	private boolean finishedConstructing = false;
	
	public AbilityManager(Game game, File folder) {
		this.game = game;
		this.folder = folder;
		this.actionManager = new GameObjectManager<>(game, AbilityAction.class);
		this.prerequisiteManager = new GameObjectManager<>(game, AbilityPrerequisite.class);
		folder.mkdirs();
	}
	
	public Game getGame() {
		return game;
	}
	
	public void loadAbilities() {
		finishedConstructing = false;
		abilities.clear();
		actionManager.clear();
		prerequisiteManager.clear();
		Files.forEachFileWithEnding(folder, ".yml", file -> createAbility(new Config(file, true)));
		finishedConstructing = true;
		abilityRequests.forEach(Worker::work);
	}
	
	public Ability createAbility(ConfigSection section) {
		return abilities.computeIfAbsent(section.getName(), name -> new Ability(this, section));
	}
	
	public void requestAbilitiesFromCommand(String originCommand, String abilityCommand, boolean requiresThrown, Consumer<? super AbilityCaller> action) {
		if(abilityCommand.contains("||")) {
			for(String command : abilityCommand.split(Pattern.quote("||"))) {
				requestAbilitiesFromCommand(originCommand, command, requiresThrown, action);
			}
		}
		else {
			if(finishedConstructing) requestAbilityFromCommand0(originCommand, abilityCommand, requiresThrown, action);
			else abilityRequests.add(() -> requestAbilityFromCommand0(originCommand, abilityCommand, requiresThrown, action));
		}
	}
	
	private void requestAbilityFromCommand0(String originCommand, String abilityCommand, boolean requiresThrown, Consumer<? super AbilityCaller> action) {
		AbilityCaller abilityCaller = getAbility(abilityCommand);
		if(abilityCaller == null) abilityCaller = createAction(abilityCommand);
		if(abilityCaller == null) System.out.println(String.format("The command '%s' requested the ability '%s' but the ability does not exist.", originCommand, abilityCommand));
		else if(requiresThrown && !abilityCaller.hasTriggerType(TriggerType.THROW))
			System.out.println(String.format("The ability '%s' needs to be thrown by command '%s', but the ability does not support being thrown.", abilityCommand, originCommand));
		else action.accept(abilityCaller);
	}
	
	public Ability getAbility(String name) {
		return abilities.get(name);
	}
	
	public void handle(SpiritPlayer player, AbilityTrigger trigger) { // TODO needs to accurately check abilitygroup
		if(!player.getCooldownManager().isCooldownActive(trigger)) {
			abilities.values().forEach(ability -> ability.trigger(player, trigger));
			if(!trigger.handleCooldownManually) trigger.cooldownHandler.startCooldown();
		}
	}
	
	public void registerActionCreator(String name, Function<GameObjectManager<AbilityAction>, AbilityAction> creator) {
		actionManager.registerCreator(name, creator);
	}
	
	public AbilityAction createAction(String action) {
		return actionManager.create(action);
	}
	
	public void registerPrerequisiteCreator(String name, Function<GameObjectManager<AbilityPrerequisite>, AbilityPrerequisite> creator) {
		prerequisiteManager.registerCreator(name, creator);
	}
	
	public AbilityPrerequisite createPrerequisite(String action) {
		return prerequisiteManager.create(action);
	}
	
	public int getAbilityId(ItemStack item) {
		return Items.getTagData(item, "AbiId", int.class);
	}
	
}
