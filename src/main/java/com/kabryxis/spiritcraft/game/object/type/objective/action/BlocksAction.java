package com.kabryxis.spiritcraft.game.object.type.objective.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.string.Strings;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BlocksAction extends SpiritGameObjectAction {
	
	private Predicate<BlockState> forPredicate;
	private BlockState block;
	
	public BlocksAction(ConfigSection creatorData) {
		super(creatorData, "blocks");
		// TODO of?
		handleSubCommand("for", true, true, string -> forPredicate = parseFor(string));
		handleSubCommand("set", true, true, string -> {
			String[] args = Strings.split(string, "-");
			int material;
			int data = 0;
			if(args.length == 1) material = Integer.parseInt(string);
			else {
				material = Integer.parseInt(args[0]);
				data = Integer.parseInt(args[1]);
			}
			block = BaseBlock.getState(material, data);
		});
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		EditSession editSession = game.getCurrentWorldData().getArena().getEditSession();
		game.getCurrentWorldData().getTotalRegion().forEach(bv -> {
			BlockState forBlock = editSession.getBlock(bv);
			if(forPredicate.test(forBlock)) {
				try {
					editSession.setBlock(bv, block);
				} catch(MaxChangedBlocksException e) {
					e.printStackTrace();
				}
			}
		});
		editSession.flushQueue();
	}
	
	private Predicate<BlockState> parseFor(String string) {
		List<Boolean> orsAndAnds = new ArrayList<>();
		for(char c : string.toCharArray()) {
			if(c == '|' || c == '&') orsAndAnds.add(c == '&');
		}
		if(orsAndAnds.size() == 0) return constructPredicate(string);
		String[] predicateArgs = string.split("[|&]");
		Predicate<BlockState> previous = null;
		for(int i = 0; i < predicateArgs.length; i++) {
			String predicateArg = predicateArgs[i];
			Predicate<BlockState> predicate = constructPredicate(predicateArg);
			if(i == 0) {
				previous = predicate;
				continue;
			}
			previous = orsAndAnds.get(i - 1) ? previous.and(predicate) : previous.or(predicate);
		}
		return previous;
	}
	
	private Predicate<BlockState> constructPredicate(String string) {
		String[] args = Strings.split(string, "-");
		if(args.length != 1) {
			String cmd = args[0].toLowerCase();
			if(cmd.equals("id")) {
				int material = Integer.parseInt(args[1]);
				return block -> block.getInternalBlockTypeId() == material;
			}
			/*else if(cmd.equals("data")) { TODO
				int data = Integer.parseInt(args[1]);
				return block -> block.getBlockType()..getData() == data;
			}*/
		}
		throw new IllegalArgumentException(String.format("Cannot parse String '%s'", string));
	}
	
}
