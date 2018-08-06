package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.kabutils.spigot.version.wrapper.packet.out.chat.WrappedPacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CountdownTask extends BukkitRunnable {
	
	private final WrappedPacketPlayOutChat packet = WrappedPacketPlayOutChat.newInstance();
	
	private final Game game;
	private final int seconds;
	
	private int timeLeft;
	
	public CountdownTask(Game game, int seconds) {
		this.game = game;
		this.seconds = seconds;
	}
	
	@Override
	public void run() {
		if(timeLeft == 0) {
			packet.setMessage("");
			game.forEachPlayer(player -> packet.send(player.getPlayer()));
			game.end();
			cancel();
			return;
		}
		int seconds = (timeLeft % 60);
		String stringSeconds = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
		packet.setMessage(ChatColor.GOLD.toString() + (timeLeft / 60) + ":" + stringSeconds);
		game.forEachPlayer(player -> packet.send(player.getPlayer()));
		timeLeft--;
	}
	
	public BukkitTask start() {
		timeLeft = seconds;
		return runTaskTimer(game.getPlugin(), 0L, 20L);
	}
	
}
