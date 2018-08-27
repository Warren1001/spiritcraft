package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityCaller;
import com.kabryxis.spiritcraft.game.a.ability.AbilityManager;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.action.AutomaticSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NearbyAction extends AutomaticSpiritAbilityAction {
	
	private final Game game;
	
	private double radius = 1.0;
	private AffectType affects = AffectType.ALL;
	private List<AbilityCaller> abilities = new ArrayList<>();
	
	public NearbyAction(AbilityManager abilityManager) {
		super("nearby");
		game = abilityManager.getGame();
		registerSubCommandHandler("radius", false, true, data -> radius = Double.parseDouble(data));
		registerSubCommandHandler("affects", false, true, data -> affects = AffectType.valueOf(data.toUpperCase()));
		registerSubCommandHandler("ability", true, false, data -> abilityManager.requestAbilitiesFromCommand(getName(), data, false, abilities::add));
	}
	
	@Override
	public void execute(Location loc) {
		AbilityTrigger trigger = new AbilityTrigger(loc);
		for(Entity entity : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
			if(entity instanceof Player) {
				SpiritPlayer spiritPlayer = game.getPlayerManager().getPlayer((Player)entity);
				if(affects == AffectType.ALL || (affects == AffectType.GHOSTS && spiritPlayer.getPlayerType() == PlayerType.GHOST) || (affects == AffectType.HUNTERS && spiritPlayer.getPlayerType() == PlayerType
						.HUNTER))
					abilities.forEach(ability -> ability.triggerSafely(spiritPlayer, trigger));
			}
		}
	}
	
	public enum AffectType {
		
		ALL, GHOSTS, HUNTERS
		
	}
	
}
