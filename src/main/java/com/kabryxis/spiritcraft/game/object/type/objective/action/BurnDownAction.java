package com.kabryxis.spiritcraft.game.object.type.objective.action;

import com.boydti.fawe.object.collection.BlockVectorSet;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;

public class BurnDownAction extends SpiritGameObjectAction {
	
	private static final BlockState FIRE_BLOCK = BaseBlock.getState(Material.FIRE.getId(), 0);
	
	public BurnDownAction(ConfigSection creatorData) {
		super(creatorData, "burn_down");
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		EditSession editSession = game.getCurrentWorldData().getArena().getEditSession();
		World world = game.getWorldManager().getWorld(editSession.getWorld().getName());
		Map<Integer, Set<BlockVector3>> positionsMap = new TreeMap<>((i1, i2) -> i2 - i1);
		game.getCurrentWorldData().getTotalRegion().forEach(bv -> {
			if(editSession.getBlock(bv).getBlockType() == BlockTypes.REDSTONE_LAMP)
				positionsMap.computeIfAbsent(bv.getBlockZ(), ignore -> new BlockVectorSet()).add(bv);
		});
		positionsMap.forEach(new BiConsumer<Integer, Set<BlockVector3>>() {
			
			private int delay = 0;
			
			@Override
			public void accept(Integer integer, Set<BlockVector3> positions) {
				if(delay == 0) {
					positions.forEach(pos -> burstIntoFlames(world, editSession, pos));
					editSession.flushQueue();
				}
				else game.getTaskManager().start(() -> {
					positions.forEach(pos -> burstIntoFlames(world, editSession, pos));
					editSession.flushQueue();
				}, delay * 2L);
				delay++;
			}
			
		});
	}
	
	protected Location getLocation(World world, BlockVector3 pos) {
		return new Location(world, pos.getX(), pos.getY(), pos.getZ());
	}
	
	protected void burstIntoFlames(World world, EditSession session, BlockVector3 vector) {
		world.createExplosion(vector.getX() + 0.5, vector.getY() + 0.5, vector.getZ() + 0.5, 5.0F, true, false);
		try {
			session.setBlock(vector, FIRE_BLOCK);
		} catch(MaxChangedBlocksException e) {
			e.printStackTrace();
		}
	}
	
}
