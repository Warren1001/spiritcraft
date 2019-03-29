package com.kabryxis.spiritcraft;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.kabryxis.kabutils.command.CommandManager;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.command.BukkitCommandIssuer;
import com.kabryxis.kabutils.spigot.command.BukkitCommandManager;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.kabutils.spigot.listener.Listeners;
import com.kabryxis.kabutils.spigot.plugin.protocollibrary.BasicReceivingPacketAdapter;
import com.kabryxis.kabutils.spigot.serialization.SpigotSerialization;
import com.kabryxis.spiritcraft.game.AttackHiddenPlayerAdapter;
import com.kabryxis.spiritcraft.game.a.game.*;
import com.kabryxis.spiritcraft.game.a.parse.CommandHandler;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.sk89q.worldedit.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.function.Function;

public class Spiritcraft extends JavaPlugin {
	
	public static final Function<String, Vector> DESERIALIZE_VECTOR = string -> {
		String[] args = string.split(",");
		return new Vector(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
	};
	
	static {
		SpigotSerialization.registerSerializers();
		ConfigSection.addDeserializer(Vector.class, DESERIALIZE_VECTOR);
		ConfigSection.addDeserializer(Material.class, Material::matchMaterial);
		CommandHandler.registerDataConverter(Vector.class, DESERIALIZE_VECTOR);
		CommandHandler.registerDataConverter(Material.class, Material::matchMaterial);
	}
	
	private Config data;
	private CommandManager commandManager;
	private SpiritGame game;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		data = new Config(new File(getDataFolder(), "config.yml"), true);
		ItemBuilder.DEFAULT.flag(ItemFlag.HIDE_ATTRIBUTES);
		Items.setupAllowNextItemSpawn(this);
		Items.setCanDropItems(true);
		ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(new AttackHiddenPlayerAdapter(this)).syncStart();
		ProtocolLibrary.getProtocolManager().addPacketListener(new BasicReceivingPacketAdapter(this, PacketType.Play.Client.SPECTATE));
		//ProtocolLibrary.getProtocolManager().addPacketListener(new BasicSendingPacketAdapter(this, event -> System.out.println(event.getPacketType()), PacketType.values()));
		game = new SpiritGame(this);
		commandManager = new BukkitCommandManager();
		commandManager.registerArgumentConverter(SpiritPlayer.class, arg -> game.getPlayerManager().getPlayer(Bukkit.getPlayer(arg)));
		commandManager.registerArgumentConverter(World.class, arg -> game.getWorldManager().getWorld(arg));
		commandManager.registerIssuerConverter(SpiritPlayer.class, issuer -> game.getPlayerManager().getPlayer(((BukkitCommandIssuer)issuer).getPlayer()));
		//commandManager.registerListener(new CommandListener(this));
		commandManager.registerListeners(new NewCommandListener(), new TestCommand(this));
		Listeners.registerListener(new LobbyListener(game), this);
		Listeners.registerListener(new GameListener(game), this, event -> game.isInProgress());
		if(getConfig().getBoolean("world-mode", false)) {
			// TODO
		}
	}
	
	@Override
	public void onDisable() {
		game.end(false);
	}
	
	public Config getData() {
		return data;
	}
	
	public SpiritGame getGame() {
		return game;
	}
	
	public void setupSQL() {
	
	}
	
}
