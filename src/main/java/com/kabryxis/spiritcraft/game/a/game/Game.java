package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.kabutils.data.MathHelp;
import com.kabryxis.kabutils.random.Randoms;
import com.kabryxis.kabutils.spigot.world.Locations;
import com.kabryxis.spiritcraft.Spiritcraft;
import com.kabryxis.spiritcraft.game.DeadBodyManager;
import com.kabryxis.spiritcraft.game.ParticleManager;
import com.kabryxis.spiritcraft.game.a.ability.AbilityManager;
import com.kabryxis.spiritcraft.game.a.ability.action.impl.*;
import com.kabryxis.spiritcraft.game.a.ability.action.impl.creator.SpiritAbilityActionCreator;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.impl.BlockPrerequisite;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.impl.HandItemPrerequisite;
import com.kabryxis.spiritcraft.game.a.ability.prerequisite.impl.creator.SpiritAbilityPrerequisiteCreator;
import com.kabryxis.spiritcraft.game.a.objective.ObjectiveManager;
import com.kabryxis.spiritcraft.game.a.objective.action.impl.RemoveHandAction;
import com.kabryxis.spiritcraft.game.a.objective.prerequisite.impl.HandPrerequisite;
import com.kabryxis.spiritcraft.game.a.parse.Parser;
import com.kabryxis.spiritcraft.game.a.parse.SpiritParser;
import com.kabryxis.spiritcraft.game.a.world.ArenaData;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundManager;
import com.kabryxis.spiritcraft.game.ability.CountdownTask;
import com.kabryxis.spiritcraft.game.inventory.InventoryManager;
import com.kabryxis.spiritcraft.game.item.ItemManager;
import com.kabryxis.spiritcraft.game.player.PlayerManager;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.GameMode;
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
	private final Parser parser;
	private final GameListener gameListener;
	private final WorldManager worldManager;
	private final PlayerManager playerManager;
	private final InventoryManager inventoryManager;
	private final ItemManager itemManager;
	private final ParticleManager particleManager;
	private final SoundManager soundManager;
	private final DeadBodyManager deadBodyManager;
	private final ObjectiveManager objectiveManager;
	private final AbilityManager abilityManager;
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
		parser = new SpiritParser();
		gameListener = new GameListener(this);
		worldManager = new WorldManager(this);
		playerManager = new PlayerManager(this, new File(plugin.getDataFolder(), "players"));
		inventoryManager = new InventoryManager(this);
		itemManager = new ItemManager(this);
		particleManager = new ParticleManager(new File(plugin.getDataFolder(), "particles"));
		soundManager = new SoundManager(new File(plugin.getDataFolder(), "sounds"));
		deadBodyManager = new DeadBodyManager(this);
		objectiveManager = new ObjectiveManager();
		objectiveManager.registerActionCreators("explode", new com.kabryxis.spiritcraft.game.a.objective.action.impl.ExplodeAction(this), "remove_hand", new RemoveHandAction());
		objectiveManager.registerPrerequisiteCreator("hand", new HandPrerequisite());
		abilityManager = new AbilityManager(this, new File(plugin.getDataFolder(), "abilities"));
		SpiritAbilityPrerequisiteCreator spiritAbilityPrerequisiteCreator = new SpiritAbilityPrerequisiteCreator(parser);
		spiritAbilityPrerequisiteCreator.registerCreator("hand", HandItemPrerequisite::new);
		spiritAbilityPrerequisiteCreator.registerCreator("block", BlockPrerequisite::new);
		abilityManager.registerPrerequisiteCreator(spiritAbilityPrerequisiteCreator);
		SpiritAbilityActionCreator spiritAbilityActionCreator = new SpiritAbilityActionCreator(parser);
		spiritAbilityActionCreator.registerCreator("cloud", CloudAction::new);
		spiritAbilityActionCreator.registerCreator("explode", ExplodeAction::new);
		spiritAbilityActionCreator.registerCreator("fire_breath", FireBreathAction::new);
		spiritAbilityActionCreator.registerCreator("looking", () -> new LookingAction(abilityManager));
		spiritAbilityActionCreator.registerCreator("nearby", () -> new NearbyAction(abilityManager));
		spiritAbilityActionCreator.registerCreator("sound", PlaySoundAction::new);
		spiritAbilityActionCreator.registerCreator("potion", PotionEffectAction::new);
		spiritAbilityActionCreator.registerCreator("remove_hand", com.kabryxis.spiritcraft.game.a.ability.action.impl.RemoveHandAction::new);
		spiritAbilityActionCreator.registerCreator("stab", StabAction::new);
		spiritAbilityActionCreator.registerCreator("throw_item_delayed", () -> new ThrowItemDelayedAction(abilityManager));
		spiritAbilityActionCreator.registerCreator("throw_item_timer", () -> new ThrowItemTimerAction(abilityManager));
		abilityManager.registerActionCreator(spiritAbilityActionCreator);
		abilityManager.loadAbilities();
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
	
	public InventoryManager getInventoryManager() {
		return inventoryManager;
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
	
	public ObjectiveManager getObjectiveManager() {
		return objectiveManager;
	}
	
	public AbilityManager getAbilityManager() {
		return abilityManager;
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
		spectators = allPlayers.stream().filter(player -> player.getPlayerType() == PlayerType.SPECTATOR).collect(Collectors.toList());
		List<SpiritPlayer> playingPlayers = allPlayers.stream().filter(player -> player.getPlayerType() == PlayerType.WAITING).collect(Collectors.toList());
		int numOfGhosts = 1;
		ghostPlayers = new ArrayList<>(numOfGhosts);
		hunterPlayers = new ArrayList<>(playingPlayers.size() - numOfGhosts);
		chooseRoles(playingPlayers, numOfGhosts, ghostPlayers, hunterPlayers);
		for(SpiritPlayer ghostPlayer : ghostPlayers) {
			ghostPlayer.teleport(currentArenaData.getNormalDimData().getRandomGhostSpawn());
			ghostPlayer.setPlayerType(PlayerType.GHOST);
			ghostPlayer.setGameMode(GameMode.SURVIVAL);
			ghostPlayer.hide();
			itemManager.giveGhostKit(ghostPlayer);
			ghostPlayer.startParticleTimer();
		}
		for(SpiritPlayer hunterPlayer : hunterPlayers) {
			hunterPlayer.teleport(currentArenaData.getNormalDimData().getRandomHunterSpawn());
			hunterPlayer.setPlayerType(PlayerType.HUNTER);
			hunterPlayer.setGameMode(GameMode.SURVIVAL);
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
		return Randoms.getRandom(players);
	}
	
	public void onEvent(Event event) {
		gameListener.onEvent(event);
	}
	
	public void end(boolean loadNext) {
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
		if(loadNext) loadNext();
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
