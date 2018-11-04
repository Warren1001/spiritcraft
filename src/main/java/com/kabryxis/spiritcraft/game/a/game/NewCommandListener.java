package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.kabutils.command.Com;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.World;

public class NewCommandListener {
	
	@Com(args = "1,4")
	public void tpw(SpiritPlayer player, int length, World world, double x, double y, double z) {
		Location loc = length == 1 ? world.getSpawnLocation() : new Location(world, x, y, z);
		player.teleport(loc);
		player.sendMessage("Teleported to %s,%s,%s,%s.", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
	}
	
	@Com(args = "1,2")
	public void game(SpiritPlayer player, int length, String string, boolean loadNext) {
		if(string.equalsIgnoreCase("start")) {
			player.sendMessage("Starting!");
			player.getGame().start();
		}
		else if(string.equalsIgnoreCase("stop") || string.equalsIgnoreCase("end")) {
			player.sendMessage("Stopping!");
			player.getGame().end(length == 1 || loadNext);
		}
	}
	
	@Com(args = "1,2")
	public void schdata(SpiritPlayer player, int length, String string1, String string2) {
		if(length == 1) {
			if(string1.equalsIgnoreCase("create")) {
				player.getDataCreator().create();
				player.sendMessage("Created schematic data.");
			}
		}
		else if(length == 2) {
			if(string1.equalsIgnoreCase("name")) {
				player.getDataCreator().name(string2);
				player.sendMessage("Set name to '%s'.", string2);
			}
			else if(string1.equalsIgnoreCase("weight")) {
				player.getDataCreator().weight(Integer.parseInt(string2));
				player.sendMessage("Set weight to '%s'.", string2);
			}
			else if(string1.equalsIgnoreCase("spawn")) {
				Location loc = player.getPlayer().getLocation();
				if(string2.equalsIgnoreCase("ghost")) {
					player.getDataCreator().addGhostSpawn(loc);
					player.sendMessage("Added %s,%s,%s to ghost spawns.", loc.getX(), loc.getY(), loc.getZ());
				}
				else if(string2.equalsIgnoreCase("hunter")) {
					player.getDataCreator().addHunterSpawn(loc);
					player.sendMessage("Added %s,%s,%s to hunter spawns.", loc.getX(), loc.getY(), loc.getZ());
				}
			}
		}
	}
	
	/*@Com(aliases = {"spirit", "sc"})
	public void spiritcraft(SpiritPlayer player, String string1) {
		if(string1.equalsIgnoreCase("join")) player.teleport(player.getGame().getSpawn());
		else if(string1.equalsIgnoreCase("leave")) player.teleport(new Location(Bukkit.getWorld("lobby"), 0.5, 101.5, 0.5)); // TODO
	}*/
	
}
