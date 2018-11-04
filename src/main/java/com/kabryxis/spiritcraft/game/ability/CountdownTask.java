package com.kabryxis.spiritcraft.game.ability;

import com.kabryxis.kabutils.spigot.version.wrapper.packet.out.chat.WrappedPacketPlayOutChat;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CountdownTask extends BukkitRunnable {
	
	private final WrappedPacketPlayOutChat packet = WrappedPacketPlayOutChat.newInstance();
	
	private final SpiritGame game;
	private final int seconds;
	
	private int timeLeft;
	
	public CountdownTask(SpiritGame game, int seconds) {
		this.game = game;
		this.seconds = seconds;
	}
	
	@Override
	public void run() {
		if(timeLeft == 0) {
			cancel();
			game.end(true);
			return;
		}
		int seconds = (timeLeft % 60);
		String stringSeconds = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
		packet.setHandle(ChatColor.GOLD.toString() + (timeLeft / 60) + ":" + stringSeconds);
		game.forEachPlayer(player -> packet.send(player.getPlayer()));
		timeLeft--;
	}
	
	@Override
	public synchronized void cancel() throws IllegalStateException {
		super.cancel();
		game.forEachPlayer(player -> WrappedPacketPlayOutChat.EMPTY.send(player.getPlayer()));
	}
	
	public BukkitTask start() {
		timeLeft = seconds;
		return runTaskTimer(game.getPlugin(), 0L, 20L);
	}
	
}
