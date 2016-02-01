package com.blazeloader.api.client.render;

/**
 * Blocks with this interface provide custom logic for rendering a TileEntity at their position.
 */
public interface TileEntityRendered {
	/**
	 * Gets the TileRenderer for this class.
	 */
	public TileRenderer getTileRenderer();
}
