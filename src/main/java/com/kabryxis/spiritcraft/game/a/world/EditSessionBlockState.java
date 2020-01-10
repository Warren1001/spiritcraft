package com.kabryxis.spiritcraft.game.a.world;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class EditSessionBlockState implements BlockState {

    private final EditSession editSession;
    private final BlockState state;

    public EditSessionBlockState(EditSession editSession, BlockState state) {
        this.editSession = editSession;
        this.state = state;
    }

    @Override
    public boolean update() {
        return update(true);
    }

    @Override
    public boolean update(boolean flush) {
        try {
            editSession.setBlock(BlockVector3.at(getX(), getY(), getZ()), BaseBlock.getState(getType().getId(), getRawData()));
            if(flush) editSession.flushQueue();
        } catch(MaxChangedBlocksException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean update(boolean flush, boolean ignore) {
        return update(flush);
    }

    /*
        default non-overridden methods
    */

    @Override
    public Block getBlock() {
        return state.getBlock();
    }

    @Override
    public MaterialData getData() {
        return state.getData();
    }
    
    @Override
    public BlockData getBlockData() {
        return state.getBlockData();
    }
    
    @Override
    public Material getType() {
        return state.getType();
    }

    @Override
    public byte getLightLevel() {
        return state.getLightLevel();
    }

    @Override
    public World getWorld() {
        return state.getWorld();
    }

    @Override
    public int getX() {
        return state.getX();
    }

    @Override
    public int getY() {
        return state.getY();
    }

    @Override
    public int getZ() {
        return state.getZ();
    }

    @Override
    public Location getLocation() {
        return state.getLocation();
    }

    @Override
    public Location getLocation(Location location) {
        return state.getLocation(location);
    }

    @Override
    public Chunk getChunk() {
        return state.getChunk();
    }

    @Override
    public void setData(MaterialData materialData) {
        state.setData(materialData);
    }
    
    @Override
    public void setBlockData(BlockData data) {
        state.setBlockData(data);
    }
    
    @Override
    public void setType(Material material) {
        state.setType(material);
    }

    @Deprecated
    @Override
    public byte getRawData() {
        return state.getRawData();
    }

    @Deprecated
    @Override
    public void setRawData(byte b) {
        state.setRawData(b);
    }

    @Override
    public boolean isPlaced() {
        return state.isPlaced();
    }

    @Override
    public void setMetadata(String s, MetadataValue metadataValue) {
        state.setMetadata(s, metadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String s) {
        return state.getMetadata(s);
    }

    @Override
    public boolean hasMetadata(String s) {
        return state.hasMetadata(s);
    }

    @Override
    public void removeMetadata(String s, Plugin plugin) {
        state.removeMetadata(s, plugin);
    }

}
