package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StabChargeTask extends ItemChargeRunnable {
	
	private static final double RADIUS = 1.1;
	
	public StabChargeTask(SpiritPlayer owner, ItemStack item) {
		super(owner, item, 3, 5);
	}
	
	@Override
	public void onStart() {
		owner.getPlayer().getWorld().playSound(owner.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 1F, 0.1F);
	}
	
	@Override
	public void onFinish() {
		BukkitThreads.syncLater(owner::resetCharge, 20L);
		Location loc = owner.getLocation();
		loc.getWorld().playSound(loc, Sound.ZOMBIE_PIG_HURT, 1F, 0.1F);
		loc = loc.add(loc.getDirection().multiply(1.25));
		Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, RADIUS, RADIUS, RADIUS);
		if(!entities.isEmpty()) {
			Set<SpiritPlayer> closePlayers = new HashSet<>();
			entities.stream().filter(Player.class::isInstance).forEach(entity -> {
				SpiritPlayer player = owner.getGame().getPlayerManager().getPlayer((Player)entity);
				if(player.getPlayerType() == PlayerType.HUNTER) closePlayers.add(player);
			});
			if(!closePlayers.isEmpty()) {
				double closestDistance = 0;
				SpiritPlayer closestPlayer = null;
				for(SpiritPlayer player : closePlayers) {
					double distance = player.getLocation().distanceSquared(loc);
					if(closestPlayer == null || distance < closestDistance) {
						closestDistance = distance;
						closestPlayer = player;
					}
				}
				closestPlayer.damage(20);
			}
		}
	}
	
}
