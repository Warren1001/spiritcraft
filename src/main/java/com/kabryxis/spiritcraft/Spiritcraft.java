package com.kabryxis.spiritcraft;

import com.comphenix.protocol.ProtocolLibrary;
import com.kabryxis.kabutils.command.CommandManager;
import com.kabryxis.kabutils.spigot.command.CommandMapHook;
import com.kabryxis.kabutils.spigot.event.Listeners;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.spiritcraft.game.AttackHiddenPlayerAdapter;
import com.kabryxis.spiritcraft.game.CommandListener;
import com.kabryxis.spiritcraft.game.a.event.PlayerChangedDimEvent;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.a.game.NewListener;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

public class Spiritcraft extends JavaPlugin {
	
	private CommandManager commandManager;
	private Game game;
	
	@Override
	public void onLoad() {
		Listeners.registerEvent(PlayerChangedDimEvent.class); // TODO contemplate the necessity of a new event
	}
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		ItemBuilder.DEFAULT.flag(ItemFlag.HIDE_ATTRIBUTES);
		ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(new AttackHiddenPlayerAdapter(this)).syncStart();
		game = new Game(this);
		commandManager = new CommandManager(ChatColor.RED + "You do not have permission to use this command!");
		commandManager.addExtraWork(new CommandMapHook(commandManager));
		commandManager.registerListener(new CommandListener(this));
		Listeners.registerListener(new NewListener(this), this);
		//Listeners.registerListener(new LobbyListener(this), new WorldExecutor(game.getWorldManager().getWorld("spirit_lobby")));
		//Listeners.registerListener(new GameListener(this), new WorldExecutor(game.getWorldManager().getWorld("spirit_overworld"), game.getWorldManager().getWorld("spirit_end"))); // TODO find better implementation
		if(getConfig().getBoolean("world-mode", false)) {
			// TODO
		}
	}
	
	public Game getGame() {
		return game;
	}
	
}
