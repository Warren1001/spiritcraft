package com.kabryxis.spiritcraft.game.a.ability.action.impl;

import com.kabryxis.spiritcraft.game.a.ability.AbilityCaller;
import com.kabryxis.spiritcraft.game.a.ability.AbilityTrigger;
import com.kabryxis.spiritcraft.game.a.ability.TriggerType;
import com.kabryxis.spiritcraft.game.a.ability.action.AbilityAction;
import com.kabryxis.spiritcraft.game.a.ability.action.AbstractSpiritAbilityAction;
import com.kabryxis.spiritcraft.game.a.game.object.GameObjectManager;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NearbyAction extends AbstractSpiritAbilityAction {
	
	private double radius = 1.0;
	private AffectType affects = AffectType.ALL;
	private int amount = Integer.MAX_VALUE;
	private List<AbilityCaller> abilities = new ArrayList<>();
	
	public NearbyAction(GameObjectManager<AbilityAction> objectManager) {
		super(objectManager, "nearby");
		handleSubCommand("radius", false, double.class, d -> radius = d);
		handleSubCommand("affects", false, true, data -> affects = AffectType.valueOf(data.toUpperCase()));
		handleSubCommand("amount", false, int.class, i -> amount = i);
		handleSubCommand("ability", true, false, data -> game.getAbilityManager().requestAbilityFromCommand(name, data, false, abilities::add));
	}
	
	@Override
	public void trigger(SpiritPlayer player, AbilityTrigger trigger) {
		Location loc = trigger.getOptimalLocation(player.getLocation());
		List<SpiritPlayer> affected = new ArrayList<>();
		loc.getWorld().getNearbyEntities(loc, radius, radius, radius).stream().filter(Player.class::isInstance).forEach(entity -> {
			SpiritPlayer spiritPlayer = game.getPlayerManager().getPlayer((Player)entity);
			if(spiritPlayer.getPlayerType() != PlayerType.SPECTATOR && (affects == AffectType.ALL || (affects == AffectType.GHOSTS && spiritPlayer.getPlayerType() == PlayerType.GHOST) ||
					(affects == AffectType.HUNTERS && spiritPlayer.getPlayerType() == PlayerType.HUNTER))) affected.add(spiritPlayer);
		});
		if(affected.size() > amount) affected.sort((p1, p2) -> (int)(p1.getLocation().distanceSquared(loc) - p2.getLocation().distanceSquared(loc)));
		AbilityTrigger nearbyTrigger = new AbilityTrigger();
		nearbyTrigger.triggerer = player;
		nearbyTrigger.type = TriggerType.NEARBY;
		nearbyTrigger.customLoc = loc;
		for(int i = 0; i < Math.min(amount, affected.size()); i++) {
			SpiritPlayer affect = affected.get(i);
			abilities.forEach(ability -> ability.triggerSafely(affect, trigger));
		}
	}
	
	public enum AffectType {
		
		ALL, GHOSTS, HUNTERS
		
	}
	
}
