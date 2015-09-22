package com.blazeloader.api.block;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

/**
 * Interface for blocks that want to specify whether a given side is solid.
 */
public interface ISided {
	
	/**
	 * Checks if the given side of this block is solid.
	 * 
	 * @forge This is part of the Forge API specification
	 * @param w		The world
	 * @param pos	Position
	 * @param side	Side being tested
	 * @param def	default value
	 * @return true if the side can be considered solid
	 */
	public boolean isSideSolid(IBlockAccess w, BlockPos pos, EnumFacing side);
}
