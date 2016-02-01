package com.blazeloader.api.client.render;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * Container for TileEntity rendering logic as a way to keep is separated from the block's code which may be running in a server.
 */
public interface TileRenderer {
	
	/**
	 * Perform any custom rendering here and then return true to stop the default behaviour.
	 * @param stack		Itemstack currently being rendered.
	 * @return	True if rendering is complete, false otherwise.
	 */
	public default boolean customRenderPass(ItemStack stack) {
		return false;
	}
	
	/**
	 * Get a tile entity instance to be used for rendering.
	 * May also perform any needed setup.<p>
	 * 
	 * It's is generally encouraged to use a single static tile entity instance rather than creating a new one each time.
	 * @param stack		Itemstack currently being rendered.
	 * @return A TileEntity
	 */
	public TileEntity getTileEntityForRender(ItemStack stack);
}
