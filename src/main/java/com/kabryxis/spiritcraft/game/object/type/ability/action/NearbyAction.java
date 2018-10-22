package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.object.TriggerType;
import com.kabryxis.spiritcraft.game.object.Triggers;
import com.kabryxis.spiritcraft.game.object.action.GameObjectAction;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NearbyAction extends SpiritGameObjectAction {
	
	private double radius = 1.0;
	private AffectType affects = AffectType.ALL;
	private int amount = Integer.MAX_VALUE;
	private List<GameObjectAction> abilities = new ArrayList<>();
	
	public NearbyAction(ConfigSection creatorData) {
		super(creatorData, "nearby");
		addRequiredObject("triggerer", SpiritPlayer.class);
		handleSubCommand("radius", false, double.class, d -> radius = d);
		handleSubCommand("affects", false, true, data -> affects = AffectType.valueOf(data.toUpperCase()));
		handleSubCommand("amount", false, int.class, i -> amount = i);
		handleSubCommand("ability", true, false, data -> game.getAbilityManager().createAction(data, abilities::add));
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		Location loc = Triggers.getOptimalLocation(triggerData);
		List<SpiritPlayer> affected = new ArrayList<>();
		loc.getWorld().getNearbyEntities(loc, radius, radius, radius).stream().filter(Player.class::isInstance).forEach(entity -> {
			SpiritPlayer spiritPlayer = game.getPlayerManager().getPlayer((Player)entity);
			if(spiritPlayer.getPlayerType() != PlayerType.SPECTATOR && (affects == AffectType.ALL || (affects == AffectType.GHOSTS && spiritPlayer.getPlayerType() == PlayerType.GHOST) ||
					(affects == AffectType.HUNTERS && spiritPlayer.getPlayerType() == PlayerType.HUNTER))) affected.add(spiritPlayer);
		});
		if(affected.size() > amount) affected.sort((p1, p2) -> (int)(p1.getLocation().distanceSquared(loc) - p2.getLocation().distanceSquared(loc)));
		ConfigSection nearbyTriggerData = new ConfigSection();
		nearbyTriggerData.put("triggerer", triggerData.get("triggerer"));
		nearbyTriggerData.put("type", TriggerType.NEARBY);
		nearbyTriggerData.put("nearbyLoc", loc);
		for(int i = 0; i < Math.min(amount, affected.size()); i++) {
			SpiritPlayer affect = affected.get(i);
			ConfigSection targetTriggerData = new ConfigSection(nearbyTriggerData).builderPut("target", affect);
			abilities.forEach(ability -> ability.perform(targetTriggerData));
		}
	}
	
	public enum AffectType {
		
		ALL, GHOSTS, HUNTERS
		
	}
	
}
