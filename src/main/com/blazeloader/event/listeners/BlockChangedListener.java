package com.blazeloader.event.listeners;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.event.listeners.args.BlockEventArgs;

/**
 * Interface for mods that handle block events
 */
public interface BlockChangedListener extends BLMod {
    /**
     * Called when a block is placed or destroyed in the world.
     *
     * @param world 		The world being changed.
     * @param position		A location for where the change has occurred
     * @param args			Addition information about this event
     * 
     * @return true to allow the change, false otherwise
     */
    public void onBlockChanged(World world, BlockPos position, BlockEventArgs args);
    
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
