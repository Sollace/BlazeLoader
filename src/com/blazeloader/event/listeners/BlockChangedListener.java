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
    
	public static class BlockEventArgs {
		private final IBlockState oldState;
		private IBlockState newState;
		private boolean canceled = false;
    	
		public BlockEventArgs(IBlockState old, IBlockState neu) {
			oldState = old;
			newState = neu;
		}
		
    	/**
    	 * Returns true if the event must be discarded.
    	 * </br>
    	 * Other mods may still receive the event after {@code cancel} is called so you should always check this first when handling an event.
    	 */
    	public boolean isCancelled() {
    		return canceled;
    	}
    	
    	/**
    	 * Cancels this event. The player will not take any fall damage or playe the landing sound.
    	 */
    	public void cancel() {
    		canceled = true;
    	}
    	
    	/**
    	 * Gets the BlockState that is being set.
    	 */
    	public IBlockState getNewState() {
    		return newState;
    	}
    	
    	/**
    	 * Changes the destination BlockState to the one given.
    	 */
    	public void setNewState(IBlockState state) {
    		if (state == null || state == oldState) {
    			cancel();
    			return;
    		}
			newState = state;
    	}
    	
    	/**
    	 * Gets the blockState that will be overriden.
    	 */
    	public IBlockState getOldState() {
    		return oldState;
    	}
	}
}
