package com.kabryxis.spiritcraft.game.a.world;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.kabryxis.kabutils.spigot.world.BlockStateManager;
import com.kabryxis.spiritcraft.game.a.game.SpiritGame;
import com.sk89q.worldedit.EditSession;
import org.bukkit.World;
import org.bukkit.block.Block;

public class EditSessionBlockStateManager extends BlockStateManager {

    private final SpiritGame game;
    private final EditSession editSession;

    public EditSessionBlockStateManager(SpiritGame game, World world) {
        super(world);
        this.game = game;
        editSession = new EditSessionBuilder(FaweAPI.getWorld(world.getName())).fastmode(false).checkMemory(false).changeSetNull()
                .limitUnlimited().allowedRegionsEverywhere().build();
    }

    @Override
    protected EditSessionBlockState getTrueBlockState(Block block) {
        return new EditSessionBlockState(editSession, super.getTrueBlockState(block));
    }

}
