package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.google.common.collect.Sets;
import com.kabryxis.kabutils.AutoRemovingQueue;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.version.custom.fallingblock.throwingblock.ThrowingBlock;
import com.kabryxis.spiritcraft.game.object.TriggerType;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BlockBreakAction extends SpiritGameObjectAction {
	
	protected static final Map<SpiritPlayer, Queue<ThrowingBlock>> brokenBlocks = new HashMap<>();
	protected static final Runnable clearBlocksWorker = () -> {
		brokenBlocks.values().forEach(Queue::clear);
		brokenBlocks.clear();
	};

	protected int amount;
	protected boolean toggle = false;
	protected int range = 0;
	protected Set<Material> ignoredTypes = Sets.newHashSet(Material.AIR);
	
	public BlockBreakAction(ConfigSection creatorData) {
		super(creatorData, "block_break", TriggerType.LEFT_CLICK, TriggerType.RIGHT_CLICK);
		game.addRoundEndTask(clearBlocksWorker);
		addRequiredObject("triggerer", SpiritPlayer.class);
		amount = game.getAbilityManager().getGlobals().getInt("block_break_amount", 8);
		handleSubCommand("toggle", false, boolean.class, b -> toggle = b);
		handleSubCommand("range", false, int.class, i -> range = i);
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		SpiritPlayer triggerer = triggerData.get("triggerer");
		if(toggle) triggerer.toggleBreakBlocks();
		else {
			Block block = triggerData.get("block_break_block", Block.class);
			Vector velocity = triggerer.getLocation().getDirection();
			velocity.setY(velocity.getY() * 0.75);
			double velY = velocity.getY();
			if(velY < 0 && block.getRelative(BlockFace.DOWN).getType() != Material.AIR
					|| velY > 0 && block.getRelative(BlockFace.UP).getType() != Material.AIR) velocity.setY(velY * -1);
			brokenBlocks.computeIfAbsent(triggerer, ignore -> new AutoRemovingQueue<>(amount)).offer(ThrowingBlock.spawn(game.getCurrentArenaData()
							.getBlockStateManager(), block, velocity, 400L, game.getCurrentArenaData()::isProtected));
		}
	}
	
	@Override
	public boolean canPerform(ConfigSection triggerData) {
		return super.canPerform(triggerData) && (toggle || (triggerData.get("triggerer", SpiritPlayer.class).canBreakBlocks() && findBlock(triggerData) &&
				!game.getCurrentArenaData().isProtected(triggerData.get("block_break_block", Block.class))));
	}
	
	private boolean findBlock(ConfigSection triggerData) {
		Block block = triggerData.get("block");
		if(block == null && range > 0) {
			block = triggerData.get("triggerer", SpiritPlayer.class).getTargetBlock(ignoredTypes, range);
			if(block.getType() == Material.AIR) block = null;
		}
		if(block != null) triggerData.put("block_break_block", block);
		return block != null;
	}
	
}
