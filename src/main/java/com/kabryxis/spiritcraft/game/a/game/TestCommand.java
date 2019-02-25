package com.kabryxis.spiritcraft.game.a.game;

import com.kabryxis.kabutils.command.Com;
import com.kabryxis.spiritcraft.Spiritcraft;
import com.kabryxis.spiritcraft.game.a.world.sound.SoundCause;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

@Com(name = "test")
public class TestCommand {
	
	private final Spiritcraft plugin;
	
	public TestCommand(Spiritcraft plugin) {
		this.plugin = plugin;
	}
	
	private BukkitTask task;
	
	@Com
	public void stop() {
		if(task != null) task.cancel();
	}
	
	@Com
	public void overloadsound(SpiritPlayer player) {
		player.getGame().getSoundManager().playSound("overload", new SoundCause(player.getLocation()));
	}
	
	@Com
	public void purge(Player player) {
		player.getWorld().getEntities().stream().filter(entity -> entity.getType() != EntityType.PLAYER).forEach(Entity::remove);
	}
	
	@Com
	public void printoffsets(SpiritPlayer player, boolean print) {
		player.getDataCreator().printOffsetLocation(print);
	}
	
	@Com
	public void inventory(SpiritPlayer player) {
		player.getGame().getItemManager().openInventory(player);
	}
	
	@Com(args = "0,1")
	public void configure(SpiritPlayer player, int length, boolean ghost) {
		player.getGame().getItemManager().openSelectedInventory(player, length == 0 || ghost);
	}
	
	@Com
	public void firewalk(SpiritPlayer player) {
		boolean b = !player.getCustomData().getBoolean("firewalk", false);
		player.getCustomData().put("firewalk", b);
		if(!b) player.getFireWalkBlocks().clear();
	}
	
	@Com
	public void rain(Player player, float f) {
		plugin.packet.getFloat().write(0, f);
	}
	
}
