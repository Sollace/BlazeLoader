package com.blazeloader.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IRotateable {
	
	/**
	 * <b>Provided for compatibility purposes.
	 * This method may be called by the forge api in place of rotateBlockTo so any implementations should override this method as well.</b>
	 * <p>
	 * Excerpt from the forge docs:<br>
	 * <br>
     * Rotate the block. For vanilla blocks this rotates around the axis passed in (generally, it should be the "face" that was hit).
     * Note: for mod blocks, this is up to the block and modder to decide. It is not mandated that it be a rotation around the
     * face, but could be a rotation to orient *to* that face, or a visiting of possible rotations.
     * The method should return true if the rotation was successful though.
     * </p>
     * @forge This is part of the Forge API Specification
     * @param world The world
     * @param pos Block position in world
     * @param axis The axis to rotate around
     * @return True if the rotation was successful, False if the rotation failed, or is not possible
     * 
     * @deprecated Use rotateBlockTo instead
     */
	@Deprecated
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis);
	
	/**
	 * Rotates this block so that it's 'face' points towards the direction indicated by the provided EnumFacing
	 * 
	 * @param w			The world
	 * @param pos		Block coordinates
	 * @param state		Current block state
	 * @param facing	Direction this block must be rotated to face towards
	 * @return	True if the rotation succeeded, false otherwise.
	 */
	public boolean rotateBlockTo(World w, BlockPos pos, IBlockState state, EnumFacing facing);
	
	/**
	 * Gets the allowed orientations for this block.
	 * 
	 * @forge This is part of the Forge API specification
	 * @param world		The world
	 * @param pos		Block coordinates
	 * @return An array of valid rotations, or null
	 */
	public EnumFacing[] getValidRotations(World world, BlockPos pos);
	
	/**
	 * Gets the current facing for this block.
	 * 
	 * @param w			The world
	 * @param pos		Block coordinates
	 * @param state 	Current block state
	 * @return An EnumFacing corresponding to this blocks orientation or null if this block cannot be rotated
	 */
	public EnumFacing getBlockRotation(World w, BlockPos pos, IBlockState state);
}
