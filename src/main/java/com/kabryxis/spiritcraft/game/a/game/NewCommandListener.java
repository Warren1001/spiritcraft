package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.kabutils.command.Com;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class NewCommandListener {
	
	@Com(args = "1,4")
	public void tpw(Player player, int length, World world, double x, double y, double z) {
		if(length == 1) player.teleport(world.getSpawnLocation());
		else player.teleport(new Location(world, x, y, z));
	}
	
	@Com
	public void game(SpiritPlayer player, String string) {
		if(string.equalsIgnoreCase("start")) {
			player.sendMessage("Starting!");
			player.getGame().start();
		}
		else if(string.equalsIgnoreCase("stop") || string.equalsIgnoreCase("end")) {
			player.sendMessage("Stopping!");
			player.getGame().end(true);
		}
	}
	
	@Com(args = "1,2")
	public void schdata(SpiritPlayer player, int length, String string1, String string2) {
		if(length == 1) {
			if(string1.equalsIgnoreCase("create")) {
				player.getCreator().create();
				player.getPlayer().sendMessage("Created schematic.");
			}
		}
		else if(length == 2) {
			if(string1.equalsIgnoreCase("name")) {
				player.getCreator().name(string2);
				player.getPlayer().sendMessage("Set name to '" + string2 + "'.");
			}
			else if(string1.equalsIgnoreCase("weight")) {
				player.getCreator().weight(Integer.parseInt(string2));
				player.getPlayer().sendMessage("Set weight to '" + string2 + "'.");
			}
			else if(string1.equalsIgnoreCase("spawn")) {
				Location loc = player.getPlayer().getLocation();
				if(string2.equalsIgnoreCase("ghost")) {
					player.getCreator().addGhostSpawn(loc);
					player.getPlayer().sendMessage("Added " + loc.getX() + "," + loc.getY() + "," + loc.getZ() + " to ghost spawns.");
				}
				else if(string2.equalsIgnoreCase("hunter")) {
					player.getCreator().addHunterSpawn(loc);
					player.getPlayer().sendMessage("Added " + loc.getX() + "," + loc.getY() + "," + loc.getZ() + " to hunter spawns.");
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
