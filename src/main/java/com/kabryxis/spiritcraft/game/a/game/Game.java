package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.kabutils.data.MathHelp;
import com.kabryxis.kabutils.random.Randoms;
import com.kabryxis.kabutils.spigot.world.Locations;
import com.kabryxis.spiritcraft.Spiritcraft;
import com.kabryxis.spiritcraft.game.DeadBodyManager;
import com.kabryxis.spiritcraft.game.ParticleManager;
import com.kabryxis.spiritcraft.game.a.world.ArenaData;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundManager;
import com.kabryxis.spiritcraft.game.ability.CountdownTask;
import com.kabryxis.spiritcraft.game.inv.ItemManager;
import com.kabryxis.spiritcraft.game.player.PlayerManager;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.event.Event;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Game {
	
	private final Spiritcraft plugin;
	private final WorldManager worldManager;
	private final PlayerManager playerManager;
	private final ItemManager itemManager;
	private final ParticleManager particleManager;
	private final SoundManager soundManager;
	private final DeadBodyManager deadBodyManager;
	private final Location lobbySpawn;
	
	private ArenaData currentArenaData;
	private List<SpiritPlayer> allPlayers;
	private List<SpiritPlayer> ghostPlayers;
	private List<SpiritPlayer> hunterPlayers;
	private List<SpiritPlayer> spectators;
	private CountdownTask countdownTask;
	private boolean inProgress = false;
	
	public Game(Spiritcraft plugin) {
		this.plugin = plugin;
		worldManager = new WorldManager(this);
		playerManager = new PlayerManager(this, new File(plugin.getDataFolder(), "players"));
		itemManager = new ItemManager(this);
		particleManager = new ParticleManager(new File(plugin.getDataFolder(), "particles"));
		soundManager = new SoundManager(new File(plugin.getDataFolder(), "sounds"));
		deadBodyManager = new DeadBodyManager(this);
		lobbySpawn = Locations.deserialize(plugin.getConfig().getString("lobby-spawn"), worldManager); // TODO
		worldManager.loadChunks(this, lobbySpawn);
		loadNext();
	}
	
	public Spiritcraft getPlugin() {
		return plugin;
	}
	
	public WorldManager getWorldManager() {
		return worldManager;
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public ItemManager getItemManager() {
		return itemManager;
	}
	
	public ParticleManager getParticleManager() {
		return particleManager;
	}
	
	public SoundManager getSoundManager() {
		return soundManager;
	}
	
	public DeadBodyManager getDeadBodyManager() {
		return deadBodyManager;
	}
	
	public Location getSpawn() {
		return lobbySpawn;
	}
	
	public ArenaData getCurrentArenaData() {
		return currentArenaData;
	}
	
	public boolean isInProgress() {
		return inProgress;
	}
	
	public void start() {
		inProgress = true;
		allPlayers = playerManager.getAllPlayers();
		spectators = allPlayers.stream().filter(player -> !player.isPlaying()).collect(Collectors.toList());
		List<SpiritPlayer> playingPlayers = allPlayers.stream().filter(SpiritPlayer::isPlaying).collect(Collectors.toList());
		int numOfGhosts = 1;
		ghostPlayers = new ArrayList<>(numOfGhosts);
		hunterPlayers = new ArrayList<>(playingPlayers.size() - numOfGhosts);
		chooseRoles(playingPlayers, numOfGhosts, ghostPlayers, hunterPlayers);
		for(SpiritPlayer ghostPlayer : ghostPlayers) {
			ghostPlayer.teleport(currentArenaData.getNormalDimData().getRandomGhostSpawn());
			ghostPlayer.setPlayerType(PlayerType.GHOST);
			ghostPlayer.hide();
			itemManager.giveGhostKit(ghostPlayer);
			ghostPlayer.startParticleTimer();
		}
		for(SpiritPlayer hunterPlayer : hunterPlayers) {
			hunterPlayer.teleport(currentArenaData.getNormalDimData().getRandomHunterSpawn());
			hunterPlayer.setPlayerType(PlayerType.HUNTER);
			itemManager.giveHunterKit(hunterPlayer);
		}
		countdownTask = new CountdownTask(this, 300);
		countdownTask.start();
	}
	
	private void chooseRoles(List<SpiritPlayer> playing, int numOfGhosts, List<SpiritPlayer> ghosts, List<SpiritPlayer> hunters) {
		List<SpiritPlayer> hasDamageAndWantsGhost = playing.stream().filter(player -> player.wantsGhost() && player.getDamageToGhost() > 0.0).collect(Collectors.toList());
		if(hasDamageAndWantsGhost.size() <= numOfGhosts) {
			ghosts.addAll(hasDamageAndWantsGhost);
			numOfGhosts -= hasDamageAndWantsGhost.size();
			if(numOfGhosts > 0) {
				List<SpiritPlayer> hasDamageToGhost = playing.stream().filter(player -> !ghosts.contains(player) && player.getDamageToGhost() > 0.0).collect(Collectors.toList());
				if(hasDamageToGhost.size() <= numOfGhosts) {
					ghosts.addAll(hasDamageToGhost);
					numOfGhosts -= hasDamageToGhost.size();
					for(int i = 0; i < numOfGhosts; i++) {
						ghosts.add(Randoms.getRandom(playing));
					}
				}
				else {
					for(int i = 0; i < numOfGhosts; i++) {
						ghosts.add(chooseWeightedRandomPlayer(hasDamageToGhost));
					}
				}
			}
		}
		else {
			for(int i = 0; i < numOfGhosts; i++) {
				ghosts.add(chooseWeightedRandomPlayer(hasDamageAndWantsGhost));
			}
		}
		allPlayers.forEach(SpiritPlayer::resetDamageToGhost);
		hunters.addAll(playing.stream().filter(player -> !ghosts.contains(player)).collect(Collectors.toList()));
	}
	
	private SpiritPlayer chooseWeightedRandomPlayer(List<SpiritPlayer> players) {
		int total = 0;
		for(SpiritPlayer player : players) {
			total += MathHelp.floor(player.getDamageToGhost() * 100.0);
		}
		int rand = new Random().nextInt(total) + 1;
		for(Iterator<SpiritPlayer> iterator = players.iterator(); iterator.hasNext();) {
			SpiritPlayer player = iterator.next();
			rand -= MathHelp.floor(player.getDamageToGhost() * 100.0);
			if(rand <= 0) {
				iterator.remove();
				return player;
			}
		}
		System.out.println("something went wrong when choosing ghost");
		return Randoms.getRandom(players);
	}
	
	public void onEvent(Event event) {
	
	}
	
	public void end() {
		if(countdownTask != null) {
			countdownTask.cancel();
			countdownTask = null;
		}
		ghostPlayers = null;
		hunterPlayers = null;
		spectators = null;
		forEachPlayer(player -> player.resetAll(lobbySpawn));
		allPlayers = null;
		inProgress = false;
		currentArenaData.unload();
		loadNext();
	}
	
	public void loadNext() {
		currentArenaData = worldManager.constructArenaData();
		currentArenaData.load();
	}
	
	public void forEachPlayer(Consumer<? super SpiritPlayer> action) {
		allPlayers.forEach(action);
	}
	
	public void forEachHunter(Consumer<? super SpiritPlayer> action) {
		hunterPlayers.forEach(action);
	}
	
}
