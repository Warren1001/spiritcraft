package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.kabutils.random.Randoms;
import com.kabryxis.kabutils.spigot.concurrent.BukkitTaskManager;
import com.kabryxis.spiritcraft.Spiritcraft;
import com.kabryxis.spiritcraft.game.DeadBodyManager;
import com.kabryxis.spiritcraft.game.ParticleManager;
import com.kabryxis.spiritcraft.game.a.parse.Parser;
import com.kabryxis.spiritcraft.game.a.parse.SpiritParser;
import com.kabryxis.spiritcraft.game.a.world.ArenaData;
import com.kabryxis.spiritcraft.game.a.world.WorldManager;
import com.kabryxis.spiritcraft.game.a.world.schematic.RoundWorldData;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundManager;
import com.kabryxis.spiritcraft.game.a.world.sound.impl.OverloadSoundSequence;
import com.kabryxis.spiritcraft.game.ability.CountdownTask;
import com.kabryxis.spiritcraft.game.inventory.InventoryManager;
import com.kabryxis.spiritcraft.game.item.ItemManager;
import com.kabryxis.spiritcraft.game.object.action.impl.ModifyHandAction;
import com.kabryxis.spiritcraft.game.object.action.impl.RemoveHandAction;
import com.kabryxis.spiritcraft.game.object.prerequisite.impl.BlockPrerequisite;
import com.kabryxis.spiritcraft.game.object.prerequisite.impl.HandPrerequisite;
import com.kabryxis.spiritcraft.game.object.type.ability.AbilityManager;
import com.kabryxis.spiritcraft.game.object.type.ability.action.*;
import com.kabryxis.spiritcraft.game.object.type.objective.ObjectiveManager;
import com.kabryxis.spiritcraft.game.object.type.objective.action.BlocksAction;
import com.kabryxis.spiritcraft.game.object.type.objective.action.BurnDownAction;
import com.kabryxis.spiritcraft.game.object.type.objective.action.ExplodeAction;
import com.kabryxis.spiritcraft.game.player.PlayerManager;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.util.NumberConversions;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SpiritGame {
	
	private final Set<Runnable> roundEndTasks = new HashSet<>();
	
	private final Spiritcraft plugin;
	private final Parser parser;
	private final BukkitTaskManager taskManager;
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
	private boolean finishedSetup = false;
	private boolean startWhenFinished = false;
	
	public SpiritGame(Spiritcraft plugin) {
		this.plugin = plugin;
		parser = new SpiritParser();
		taskManager = new BukkitTaskManager(plugin);
		worldManager = new WorldManager(this);
		playerManager = new PlayerManager(this, new File(plugin.getDataFolder(), "players"));
		inventoryManager = new InventoryManager(this);
		itemManager = new ItemManager(this);
		particleManager = new ParticleManager(new File(plugin.getDataFolder(), "particles")); // TODO compile to one file
		soundManager = new SoundManager(plugin.getDataFolder());
		soundManager.registerSoundPlayer(new OverloadSoundSequence(this));
		deadBodyManager = new DeadBodyManager(this);
		objectiveManager = new ObjectiveManager(this);
		objectiveManager.registerPrerequisiteCreator("hand", HandPrerequisite::new);
		objectiveManager.registerActionCreator("explode", ExplodeAction::new);
		objectiveManager.registerActionCreator("remove_hand", RemoveHandAction::new);
		objectiveManager.registerActionCreator("blocks", BlocksAction::new);
		objectiveManager.registerActionCreator("burn_down", BurnDownAction::new);
		abilityManager = new AbilityManager(this, new File(plugin.getDataFolder(), "abilities"));
		abilityManager.registerPrerequisiteCreator("hand", HandPrerequisite::new);
		abilityManager.registerPrerequisiteCreator("block", BlockPrerequisite::new);
		abilityManager.registerActionCreator("trailing_arrow", TrailingArrowAction::new);
		abilityManager.registerActionCreator("modify_hand", ModifyHandAction::new);
		abilityManager.registerActionCreator("block_break", BlockBreakAction::new);
		abilityManager.registerActionCreator("charge", ChargeAction::new);
		abilityManager.registerActionCreator("cloud", CloudAction::new);
		abilityManager.registerActionCreator("explode", com.kabryxis.spiritcraft.game.object.type.ability.action.ExplodeAction::new);
		abilityManager.registerActionCreator("fire_breath", FireBreathAction::new);
		abilityManager.registerActionCreator("infront", InFrontAction::new);
		abilityManager.registerActionCreator("looking", LookingAction::new);
		abilityManager.registerActionCreator("nearby", NearbyAction::new);
		abilityManager.registerActionCreator("player", PlayerAction::new);
		abilityManager.registerActionCreator("sound", PlaySoundAction::new);
		abilityManager.registerActionCreator("potion", PotionEffectAction::new);
		abilityManager.registerActionCreator("remove_hand", RemoveHandAction::new);
		abilityManager.registerActionCreator("throw_item_delayed", ThrowItemDelayedAction::new);
		abilityManager.registerActionCreator("throw_item_timer", ThrowItemTimerAction::new);
		abilityManager.loadAbilities();
		lobbySpawn = plugin.getData().getCustom("lobby-spawn", Location.class);
		worldManager.loadChunks(this, lobbySpawn, 9);
		loadNext();
	}
	
	public Spiritcraft getPlugin() {
		return plugin;
	}
	
	public Parser getParser() {
		return parser;
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
	
	public BukkitTaskManager getTaskManager() {
		return taskManager;
	}
	
	public Location getSpawn() {
		return lobbySpawn;
	}
	
	public ArenaData getCurrentArenaData() {
		return currentArenaData;
	}
	
	public void addRoundEndTask(Runnable runnable) {
		roundEndTasks.add(runnable);
	}
	
	public boolean isInProgress() {
		return inProgress;
	}
	
	public boolean isLoaded() {
		return currentArenaData != null;
	}
	
	public void finishSetup() {
		finishedSetup = true;
		if(startWhenFinished) start();
	}
	
	public void start() {
		if(!finishedSetup) {
			startWhenFinished = true;
			return;
		}
		inProgress = true;
		allPlayers = playerManager.getAllPlayers();
		spectators = allPlayers.stream().filter(player -> player.getPlayerType() == PlayerType.SPECTATOR).collect(Collectors.toList());
		List<SpiritPlayer> playingPlayers = allPlayers.stream().filter(player -> player.getPlayerType() == PlayerType.WAITING).collect(Collectors.toList());
		int numOfGhosts = 1;
		ghostPlayers = new ArrayList<>(numOfGhosts);
		hunterPlayers = new ArrayList<>(playingPlayers.size() - numOfGhosts);
		chooseRoles(playingPlayers, numOfGhosts, ghostPlayers, hunterPlayers);
		for(SpiritPlayer ghostPlayer : ghostPlayers) {
			ghostPlayer.teleport(currentArenaData.getRandomGhostSpawn());
			ghostPlayer.setPlayerType(PlayerType.GHOST);
			ghostPlayer.setGameMode(GameMode.SURVIVAL);
			ghostPlayer.hide();
			itemManager.giveGhostKit(ghostPlayer);
			ghostPlayer.startParticleTimer();
		}
		for(SpiritPlayer hunterPlayer : hunterPlayers) {
			hunterPlayer.teleport(currentArenaData.getRandomHunterSpawn());
			hunterPlayer.setPlayerType(PlayerType.HUNTER);
			hunterPlayer.setGameMode(GameMode.SURVIVAL);
			itemManager.giveHunterKit(hunterPlayer);
		}
		currentArenaData.relight();
		countdownTask = new CountdownTask(this, 300);
		countdownTask.start();
	}
	
	protected void chooseRoles(List<SpiritPlayer> playing, int numOfGhosts, List<SpiritPlayer> ghosts, List<SpiritPlayer> hunters) {
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
						ghosts.add(Randoms.getRandomAndRemove(playing));
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
	
	protected SpiritPlayer chooseWeightedRandomPlayer(List<SpiritPlayer> players) {
		int total = 0;
		for(SpiritPlayer player : players) {
			total += NumberConversions.floor(player.getDamageToGhost() * 100.0);
		}
		int rand = new Random().nextInt(total) + 1;
		for(Iterator<SpiritPlayer> iterator = players.iterator(); iterator.hasNext();) {
			SpiritPlayer player = iterator.next();
			rand -= NumberConversions.floor(player.getDamageToGhost() * 100.0);
			if(rand <= 0) {
				iterator.remove();
				return player;
			}
		}
		return Randoms.getRandom(players);
	}
	
	public void end(boolean loadNext) {
		if(!isLoaded()) return;
		inProgress = false;
		finishedSetup = false;
		startWhenFinished = false;
		roundEndTasks.forEach(Runnable::run);
		if(countdownTask != null) {
			countdownTask.cancel();
			countdownTask = null;
		}
		ghostPlayers = null;
		hunterPlayers = null;
		spectators = null;
		if(allPlayers != null) forEachPlayer(player -> player.resetAll(lobbySpawn));
		allPlayers = null;
		currentArenaData.unload();
		currentArenaData = null;
		if(loadNext) loadNext();
	}
	
	public void loadNext() {
		currentArenaData = worldManager.constructArenaData();
		currentArenaData.load();
		RoundWorldData worldData = worldManager.getWorldDataManager().create();
		worldData.load();
	}
	
	public void forEachPlayer(Consumer<? super SpiritPlayer> action) {
		allPlayers.forEach(action);
	}
	
	public void forEachHunter(Consumer<? super SpiritPlayer> action) {
		hunterPlayers.forEach(action);
	}
	
}
