package com.kabryxis.spiritcraft.game.player;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PlayerManager {
	
	private final Map<UUID, SpiritPlayer> playersMap = new HashMap<>();
	
	private final SpiritGame game;
	private final File folder;
	
	public PlayerManager(SpiritGame game, File folder) {
		this.game = game;
		this.folder = folder;
		folder.mkdirs();
	}
	
	public SpiritGame getGame() {
		return game;
	}
	
	public File getFolder() {
		return folder;
	}
	
	public SpiritPlayer getPlayer(Player player) {
		return playersMap.computeIfAbsent(player.getUniqueId(), u -> new SpiritPlayer(game, u, new Config(new File(folder, u.toString() + ".yml"), true)));
	}
	
	public List<SpiritPlayer> getPlayers(Predicate<? super SpiritPlayer> predicate) {
		return playersMap.values().stream().filter(predicate).collect(Collectors.toList());
	}
	
	public List<SpiritPlayer> getAllPlayers() {
		return new ArrayList<>(playersMap.values());
	}
	
	public void forEachPlayer(Consumer<? super SpiritPlayer> action) {
		playersMap.values().forEach(action);
	}
	
}
