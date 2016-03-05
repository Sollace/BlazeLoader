package com.blazeloader.event.listeners.args;

import net.minecraft.block.state.IBlockState;

public class BlockEventArgs extends Cancellable {
	private final IBlockState oldState;
	private IBlockState newState;
	
	public BlockEventArgs(IBlockState old, IBlockState neu) {
		oldState = old;
		newState = neu;
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