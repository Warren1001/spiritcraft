package com.kabryxis.spiritcraft;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.kabryxis.kabutils.command.CommandManager;
import com.kabryxis.kabutils.spigot.command.CommandMapHook;
import com.kabryxis.kabutils.spigot.event.Listeners;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.spiritcraft.game.AttackHiddenPlayerAdapter;
import com.kabryxis.spiritcraft.game.CommandListener;
import com.kabryxis.spiritcraft.game.a.event.PlayerChangedDimEvent;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.a.game.LobbyListener;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

public class Spiritcraft extends JavaPlugin {
	
	private CommandManager commandManager;
	private Game game;
	private LobbyListener listener;
	
	@Override
	public void onLoad() {
		Listeners.registerEvent(PlayerChangedDimEvent.class); // TODO contemplate the necessity of a new event
	}
	
	@Override
	public void onDisable() {
		if(game.isInProgress()) game.end(false);
	}
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		ItemBuilder.DEFAULT.flag(ItemFlag.HIDE_ATTRIBUTES);
		ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(new AttackHiddenPlayerAdapter(this)).syncStart();
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Client.SPECTATE) {
			
			@Override
			public void onPacketReceiving(PacketEvent event) {
				event.setCancelled(true);
			}
			
		});
		game = new Game(this);
		commandManager = new CommandManager(ChatColor.RED + "You do not have permission to use this command!");
		commandManager.addExtraWork(new CommandMapHook(commandManager));
		commandManager.registerListener(new CommandListener(this));
		listener = new LobbyListener(game);
		Listeners.registerListener(listener, this);
		//Listeners.registerListener(new LobbyListener(this), new WorldExecutor(game.getWorldManager().getWorld("spirit_lobby")));
		//Listeners.registerListener(new GameListener(this), new WorldExecutor(game.getWorldManager().getWorld("spirit_overworld"), game.getWorldManager().getWorld("spirit_end"))); // TODO find better implementation
		if(getConfig().getBoolean("world-mode", false)) {
			// TODO
		}
	}
	
	public Game getGame() {
		return game;
	}
	
	public void allowNextItemSpawn() {
		listener.allowNextItemSpawn();
	}
	
}
