package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.AutoRemovable;
import com.kabryxis.kabutils.AutoRemovingQueue;
import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.listener.Listeners;
import com.kabryxis.kabutils.spigot.version.custom.fallingblock.throwingblock.ThrowingBlock;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.kabryxis.spiritcraft.game.object.TriggerType;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.kabryxis.spiritcraft.game.player.PlayerType;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.inventivetalent.particle.ParticleEffect;

import java.util.*;

public class BlockBreakAction extends SpiritGameObjectAction {
	
	protected static final Map<SpiritPlayer, Queue<WrappedThrowingBlock>> brokenBlocks = new HashMap<>();
	
	protected static WrappedThrowingBlocksTask throwingBlocksTask;
	
	private static void setup(SpiritGame game) {
		if(throwingBlocksTask == null) {
			throwingBlocksTask = Listeners.registerListener(new WrappedThrowingBlocksTask(game), game.getPlugin());
			game.getTaskManager().start(throwingBlocksTask, 0L, 1L);
		}
	}
	
	protected int amount;
	protected boolean toggle = false;
	protected int range = 0;
	
	public BlockBreakAction(ConfigSection creatorData) {
		super(creatorData, "block_break", TriggerType.LEFT_CLICK, TriggerType.RIGHT_CLICK);
		setup(game);
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
			brokenBlocks.computeIfAbsent(triggerer, ignore -> new AutoRemovingQueue<>(amount)).offer(throwingBlocksTask.spawnThrowingBlock(block, velocity));
		}
	}
	
	@Override
	public boolean canPerform(ConfigSection triggerData) {
		return super.canPerform(triggerData) && (toggle || (triggerData.get("triggerer", SpiritPlayer.class).canBreakBlocks()
				&& findBlock(triggerData) && !game.getCurrentArenaData().isProtected(triggerData.get("block_break_block", Block.class))));
	}
	
	private boolean findBlock(ConfigSection triggerData) {
		Block block = triggerData.get("block");
		if(block == null && range > 0) {
			block = triggerData.get("triggerer", SpiritPlayer.class).getTargetBlock(range);
			if(block.getType() == Material.AIR) block = null;
		}
		if(block != null) triggerData.put("block_break_block", block);
		return block != null;
	}
	
	public static class WrappedThrowingBlock implements AutoRemovable {
		
		protected final Block origin;
		protected final BlockState originState;
		protected final ItemStack effectData;
		
		protected ThrowingBlock throwingBlock;
		protected Block block;
		
		public WrappedThrowingBlock(Block origin, Vector velocity) {
			this.origin = origin;
			this.originState = origin.getState();
			this.effectData = new ItemStack(originState.getType(), originState.getRawData());
			origin.setType(Material.AIR, false);
			playEffect(origin);
			throwingBlock = ThrowingBlock.spawn(origin.getLocation().add(0.5, 0.5, 0.5), originState.getType(), originState.getRawData(), 400);
			throwingBlock.getBukkitEntity().setVelocity(velocity);
		}
		
		public ThrowingBlock getThrowingBlock() {
			return throwingBlock;
		}
		
		public void changed(Block block) {
			throwingBlock = null;
			this.block = block;
			playEffect(block);
		}
		
		public boolean sameXZ(Block block) {
			return origin.getWorld().equals(block.getWorld()) && origin.getX() == block.getX() && origin.getZ() == block.getZ();
		}
		
		@Override
		public void remove() {
			if(throwingBlock != null) throwingBlock.forceRemove();
			if(block != null) {
				block.setType(Material.AIR, false); // TODO may need to record origin type/data of block isntead of setting to air
				playEffect(block);
			}
			origin.setTypeIdAndData(originState.getTypeId(), originState.getRawData(), false);
			playEffect(origin);
		}
		
		private void playEffect(Block block) {
			ParticleEffect.BLOCK_DUST.sendData(block.getWorld().getPlayers(), block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5, 0.5, 0.5, 0.5, 0, 70, effectData);
			block.getWorld().playSound(block.getLocation(), Sound.STEP_STONE, 2F, 1F);
		}
		
	}
	
	public static class WrappedThrowingBlocksTask implements Runnable, Listener {
		
		private final Map<Long, WrappedThrowingBlock> throwingBlockMap = new HashMap<>();
		private final Set<Entity> fallingBlocks = new HashSet<>();
		private final double damageRadius = 0.45;
		
		private final SpiritGame game;
		
		public WrappedThrowingBlocksTask(SpiritGame game) {
			this.game = game;
		}
		
		public WrappedThrowingBlock spawnThrowingBlock(Block block, Vector velocity) {
			WrappedThrowingBlock wrappedThrowingBlock = new WrappedThrowingBlock(block, velocity);
			ThrowingBlock throwingBlock = wrappedThrowingBlock.getThrowingBlock();
			throwingBlockMap.put(throwingBlock.getUniqueId(), wrappedThrowingBlock);
			fallingBlocks.add(throwingBlock.getBukkitEntity());
			return wrappedThrowingBlock;
		}
		
		@Override
		public void run() {
			for(Iterator<Entity> it = fallingBlocks.iterator(); it.hasNext();) {
				Entity entity = it.next();
				if(!entity.isValid() || entity.isDead()) {
					it.remove();
					return;
				}
				for(Entity nearby : entity.getNearbyEntities(damageRadius, damageRadius, damageRadius)) {
					if(entity instanceof Player) {
						Player bukkitPlayer = (Player)entity;
						SpiritPlayer player = game.getPlayerManager().getPlayer(bukkitPlayer);
						if(player.getPlayerType() == PlayerType.HUNTER) bukkitPlayer.damage(1, entity);
					}
				}
			}
		}
		
		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		public void onEntityChangeBlock(EntityChangeBlockEvent event) {
			ThrowingBlock throwingBlock = ThrowingBlock.cast(event.getEntity());
			if(throwingBlock != null) {
				WrappedThrowingBlock wrappedThrowingBlock = throwingBlockMap.get(throwingBlock.getUniqueId());
				if(!throwingBlock.canBePlaced()) {
					event.setCancelled(true);
					return;
				}
				throwingBlockMap.remove(throwingBlock.getUniqueId());
				fallingBlocks.remove(throwingBlock.getBukkitEntity());
				Block block = event.getBlock();
				if(wrappedThrowingBlock.sameXZ(block)) {
					throwingBlock.forceRemove();
					event.setCancelled(true);
					return;
				}
				game.getTaskManager().start(() -> wrappedThrowingBlock.changed(block));
			}
		}
		
	}
	
}
