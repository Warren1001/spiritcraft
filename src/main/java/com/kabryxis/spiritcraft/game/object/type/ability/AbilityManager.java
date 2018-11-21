package com.kabryxis.spiritcraft.game.object.type.ability;

import com.kabryxis.kabutils.data.file.Files;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.a.cooldown.CooldownHandler;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.object.action.GameObjectAction;
import com.kabryxis.spiritcraft.game.object.prerequisite.GameObjectPrerequisite;
import com.kabryxis.spiritcraft.game.object.type.GameObjectTypeManager;

import java.io.File;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class AbilityManager extends GameObjectTypeManager {
	
	private final File folder;
	private final Config globals;
	
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
		gameObjectBases.clear();
		actionManager.clear();
		prerequisiteManager.clear();
		Files.forEachFileWithEnding(folder, Config.EXTENSION, file -> {
			if(!Files.getSimpleName(file).toLowerCase().equals("globals")) createBase(new Config(file, true));
		});
	}
	
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
		triggerData.put("handleCooldownManually", false);
		super.perform(triggerData);
		if(!triggerData.getBoolean("handleCooldownManually", false)) triggerData.get("cooldownHandler", CooldownHandler.class).startCooldown();
	}
	
}
