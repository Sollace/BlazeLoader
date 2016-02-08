package com.blazeloader.event.listeners;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.blazeloader.bl.mod.BLMod;

/**
 * Interface for mods that handle block events
 */
public interface BlockChangedListener extends BLMod {
    /**
     * Called when a block is placed or destroyed in the world.
     *
     * @param world 		The world being changed.
     * @param position		A location for where the change has occurred
     * @param oldState		The previous block state for that location
     * @param newState		What the block state for that location has become
     */
    public void onBlockChanged(World world, BlockPos position, IBlockState oldState, IBlockState newState);
    
    /**
     * Called when a player destroys a block.
     * 
     * @param player	The player making the change
     * @param world		The world being changed
     * @param state		New block state
     * @param pos		Location of change
     */
    public void onBreakBlock(EntityPlayer player, World world, IBlockState state, BlockPos pos);
    
    /**
     * Called when a player places a block.
     * 
     * @param player	The player making the change
     * @param world		The world being changed
     * @param state		New block state
     * @param pos		Location of change
     */
    public void onPlaceBlock(EntityPlayer player, World world, IBlockState state, BlockPos pos);
}
