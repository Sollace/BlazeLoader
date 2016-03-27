package com.blazeloader.api.block.fluid;

import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Base class for any block of flowing liquid.
 */
public abstract class FluidFlowing extends BlockDynamicLiquid implements Fluid {

	protected FluidFlowing(Material materialIn) {
		super(materialIn);
	}
	
	/**
	 * Stops flowing and converts this block into it's static variant.
	 */
	@Override
	protected void placeStaticBlock(World worldIn, BlockPos pos, IBlockState currentState) {
        worldIn.setBlockState(pos, getStaticBlock().getDefaultState().withProperty(LEVEL, currentState.getValue(LEVEL)), 2);
    }
	
	/**
	 * Gets the direction in which this fluid is flowing.
	 */
	@Override
	public Vec3 getFlowVector(IBlockAccess worldIn, BlockPos pos) {
		return super.getFlowVector(worldIn, pos);
	}
}
