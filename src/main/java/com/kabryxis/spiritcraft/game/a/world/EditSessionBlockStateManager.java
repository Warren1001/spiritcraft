package com.kabryxis.spiritcraft.game.a.world;

import com.kabryxis.kabutils.spigot.world.BlockStateManager;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import org.bukkit.World;
import org.bukkit.block.Block;

public class EditSessionBlockStateManager extends BlockStateManager {

    private final SpiritGame game;

    public EditSessionBlockStateManager(SpiritGame game, World world) {
        super(world);
        this.game = game;
    }

    @Override
    protected EditSessionBlockState getTrueBlockState(Block block) {
        return new EditSessionBlockState(game.getWorldManager().getEditSession(block.getWorld(), false), super.getTrueBlockState(block));
    }

}
