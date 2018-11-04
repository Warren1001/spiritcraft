package com.kabryxis.spiritcraft.game.object.type.ability;

import com.kabryxis.kabutils.data.file.Files;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownHandler;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.object.action.GameObjectAction;
import com.kabryxis.spiritcraft.game.object.prerequisite.GameObjectPrerequisite;
import com.kabryxis.spiritcraft.game.object.type.GameObjectTypeManager;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;

import java.io.File;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class AbilityManager extends GameObjectTypeManager {
	
	//private final Set<Worker> abilityRequests = new HashSet<>();
	
	private final File folder;
	private final Config globals;
	
	//private boolean finishedConstructing = false;
	
	public AbilityManager(SpiritGame game, File folder) {
		super(game, GameObjectAction.class, GameObjectPrerequisite.class);
		this.folder = folder;
		this.globals = new Config(new File(folder, "globals.yml"), true);
		folder.mkdirs();
	}
	
	public Config getGlobals() {
		return globals;
	}
	
	public void loadAbilities() {
		//finishedConstructing = false;
		gameObjectBases.clear();
		actionManager.clear();
		prerequisiteManager.clear();
		Files.forEachFileWithEnding(folder, Config.EXTENSION, file -> {
			if(!Files.getSimpleName(file).toLowerCase().equals("globals")) createBase(new Config(file, true));
		});
		//finishedConstructing = true;
		//abilityRequests.forEach(Worker::work);
	}
	
	/*public void requestAbilitiesFromCommand(String originCommand, String abilityCommand, boolean requiresThrown, Consumer<? super GameObjectAction> action) {
		if(abilityCommand.contains("||")) { // TODO detect top level, currently splits ALL ||, unintended
			for(String command : abilityCommand.split(Pattern.quote("||"))) {
				requestAbilitiesFromCommand(originCommand, command, requiresThrown, action);
			}
		}
		else {
			if(finishedConstructing) requestAbilityFromCommand0(originCommand, abilityCommand, requiresThrown, action);
			else abilityRequests.add(() -> requestAbilityFromCommand0(originCommand, abilityCommand, requiresThrown, action));
		}
	}
	
	private void requestAbilityFromCommand0(String originCommand, String abilityCommand, boolean requiresThrown, Consumer<? super GameObjectAction> action) {
		GameObjectAction objectAction = createAction(abilityCommand);
		if(objectAction == null) System.out.println(String.format("The command '%s' requested the ability '%s' but the ability does not exist.", originCommand, abilityCommand));
		else if(requiresThrown && !objectAction.hasTriggerType(TriggerType.THROW))
			System.out.println(String.format("The ability '%s' needs to be thrown by command '%s', but the ability does not support being thrown.", abilityCommand, originCommand));
		else action.accept(objectAction);
	}*/
	
	public void createAction(String command, Consumer<? super GameObjectAction> action) {
		if(command.contains("||")) { // TODO detect top level, currently splits ALL ||, unintended
			for(String subCommand : command.split(Pattern.quote("||"))) {
				createAction(subCommand, action);
			}
		}
		else action.accept(createAction(command));
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		SpiritPlayer triggerer = triggerData.get("triggerer");
		if(triggerer == null) return;
		if(!triggerer.getCooldownManager().isCooldownActive(triggerData)) {
			triggerData.put("handleCooldownManually", false);
			super.perform(triggerData);
			if(!triggerData.getBoolean("handleCooldownManually", false)) triggerData.get("cooldownHandler", CooldownHandler.class).startCooldown();
		}
	}
	
}
