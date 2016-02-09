package com.blazeloader.api.client.render;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class ApiRenderTileEntity {
	/**
	 * Checks if there is a renderer registered for the given type of TileEntity.
	 * 
	 * @param clazz		Tile entity class
	 * @return	True if there is a renderer, false otherwise.
	 */
	public static boolean hasSpecialRenderer(Class<? extends TileEntity> clazz) {
		return getSpecialRenderer(clazz) != null;
	}
	
	/**
	 * Checks if there is a renderer registered for the given TileEntity.
	 * 
	 * @param tileEntity		Tile entity
	 * @return	True if there is a renderer, false otherwise.
	 */
	public static <T extends TileEntity> boolean hasSpecialRenderer(T tileEntity) {
		return TileEntityRendererDispatcher.instance.hasSpecialRenderer(tileEntity);
	}
	
	/**
	 * Gets a renderer for the given type of TileEntity.
	 * If one is not registered will check for any renderer's associated with this one's parents.
	 * 
	 * @param clazz		Tile entity class
	 * @return	A renderer that may be used with this tile entity class.
	 */
	public static TileEntitySpecialRenderer getSpecialRenderer(Class<? extends TileEntity> clazz) {
		return TileEntityRendererDispatcher.instance.getSpecialRendererByClass(clazz);
	}
	
	/**
	 * Gets a renderer for the given TileEntity.
	 * If one is not registered will check for any renderer's associated with this one's parent classes.
	 * 
	 * @param tileEntity		Tile entity
	 * @return	A renderer that may be used with this tile entity.
	 */
	public static <T extends TileEntity> TileEntitySpecialRenderer getSpecialRenderer(T tileEntity) {
		return TileEntityRendererDispatcher.instance.getSpecialRenderer(tileEntity);
	}
	
	/**
	 * Adds a Renderer for the given type of TileEntity.
	 * 
	 * @param clazz		Tile entity class
	 * @param renderer	The renderer to use
	 */
	public static void registerSpecialRenderer(Class<? extends TileEntity> clazz, TileEntitySpecialRenderer renderer) {
		TileEntityRendererDispatcher.instance.mapSpecialRenderers.put(clazz, renderer);
	}
	
	/**
	 * Renders a TileEntity at the given coordinates.
	 * 
	 * @param tile			TileEntity to render
	 * @param x				X-coordinate
	 * @param y				Y-coordinate
	 * @param z				Z-coordinate
	 * @param partialTicks	Partial render ticks
	 * @param destroyStage	Destruction amount, or -1 for no destruction
	 */
	public static void renderTileEntity(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage) {
		TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, x, y, z, partialTicks, destroyStage);
	}
}
