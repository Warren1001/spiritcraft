package com.kabryxis.spiritcraft.game.inv;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.kabryxis.spiritcraft.game.a.game.Game;
import com.kabryxis.spiritcraft.game.inv.utility.ErrorAction;
import com.kabryxis.spiritcraft.game.inv.utility.InformationAction;
import com.kabryxis.spiritcraft.game.inv.utility.PreviousInventoryAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.kabutils.spigot.inventory.itemstack.ItemBuilder;
import com.kabryxis.kabutils.spigot.version.Version;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;

public class ItemManager implements Listener {
	
	private final UtilityClickAction previousInventoryAction = new PreviousInventoryAction();
	private final UtilityClickAction informationAction = new InformationAction();
	private final UtilityClickAction errorAction = new ErrorAction();
	
	private final Game game;
	private final Config globalItemData;
	private final ItemData ghostData;
	private final ItemData hunterData;
	private final HubInventory start;
	
	public ItemManager(Game game) {
		this.game = game;
		globalItemData = new Config(new File(game.getPlugin().getDataFolder(), "items.yml"));
		ghostData = new ItemData(this, true);
		hunterData = new ItemData(this, false);
		globalItemData.load(config -> {
			ghostData.reload();
			hunterData.reload();
		});
		start = new HubInventory(this, ChatColor.GOLD + "Choose Class type to modify", 1);
		start.setUtility(0, previousInventoryAction);
		start.setUtility(4, informationAction);
		start.setUtility(8, errorAction);
		DynamicInventory ghostInv = ghostData.getInventory();
		start.set(2, ghostInv, ItemBuilder.newItemBuilder(Material.SNOW_BALL).name(ChatColor.DARK_GRAY + "Ghost").build());
		DynamicInventory hunterInv = hunterData.getInventory();
		start.set(6, hunterInv, ItemBuilder.newItemBuilder(Material.CHAINMAIL_CHESTPLATE).name(ChatColor.DARK_RED + "Hunter").build());
		game.getPlugin().getServer().getPluginManager().registerEvents(this, game.getPlugin());
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(game.getPlugin(), PacketType.Play.Server.WINDOW_ITEMS) {
			
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				SpiritPlayer player = game.getPlayerManager().getPlayer(event.getPlayer());
				DynamicInventory inventory = player.getCurrent();
				if(inventory != null) {
					ItemStack[] items = getFromModifier(packet);
					inventory.modifyItems(items, player);
					writeToModifier(packet, items);
				}
			}
			
		});
		
	}
	
	public Game getGame() {
		return game;
	}
	
	public Config getGlobalItemData() {
		return globalItemData;
	}
	
	public ItemData getItemData(boolean ghost) {
		return ghost ? ghostData : hunterData;
	}
	
	public UtilityClickAction getPreviousInventoryAction() {
		return previousInventoryAction;
	}
	
	public UtilityClickAction getInformationAction() {
		return informationAction;
	}
	
	public UtilityClickAction getErrorAction() {
		return errorAction;
	}
	
	public void openInventory(SpiritPlayer player) {
		player.openInventory(start);
	}
	
	public void giveGhostKit(SpiritPlayer player) {
		player.getPlayerItemInfo(true).giveKit();
	}
	
	public void giveHunterKit(SpiritPlayer player) {
		player.getPlayerItemInfo(false).giveKit();
	}
	
	private void writeToModifier(PacketContainer packet, ItemStack[] items) {
		if(Version.VERSION.isVersionAtLeast(Version.v1_11_R1)) packet.getItemListModifier().write(0, Arrays.asList(items));
		else packet.getItemArrayModifier().write(0, items);
	}
	
	private ItemStack[] getFromModifier(PacketContainer packet) {
		return Version.VERSION.isVersionAtLeast(Version.v1_11_R1) ? packet.getItemListModifier().read(0).toArray(new ItemStack[0]) : packet.getItemArrayModifier().read(0);
	}
	
	public void openSelectedInventory(SpiritPlayer player, boolean ghost) {
		player.openInventory(new PlayerConfigureInventory(this, ChatColor.GOLD + "Configure your inventory", ghost));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event) {
		SpiritPlayer player = game.getPlayerManager().getPlayer((Player)event.getPlayer());
		DynamicInventory current = player.getCurrent();
		if(current != null && event.getView().getTopInventory().getTitle().equals(current.getTitle())) {
			player.archiveInventory();
			current.onClose(player);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent event) {
		game.getPlugin().getServer().getPluginManager().callEvent(new InventoryCloseEvent(game.getPlayerManager().getPlayer((Player)event.getPlayer()).getPlayer().getOpenInventory()));
	}
	
}
