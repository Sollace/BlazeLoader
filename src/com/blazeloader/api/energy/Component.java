package com.blazeloader.api.energy;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Main interface to represent a component or machine that works with energy.
 * Modders would commonly implement this on their TileEntities
 *
 * @param <T>	The units of energy this Component works with.
 */
public interface Component<T extends SIUnit> {
	
	/**
	 * Checks if this component is capable or transferring energy in the specified direction.
	 * 
	 * @param w		The current world
	 * @param pos	Block position of this component
	 * @param side	The side being checked against
	 * 
	 * @return	True if other machines may transfer with this component.
	 */
	public boolean canTransferEnergy(World w, BlockPos pos, EnumFacing side);
	
	/**
	 * Gets a Node for this machine
	 * 
	 * @param w		The current world
	 * @param pos	Block position of this component
	 * @param side	The side being checked against
	 **/
	public Node<T> getNode(Component<? extends SIUnit> other, World w, BlockPos pos, EnumFacing side);
}
