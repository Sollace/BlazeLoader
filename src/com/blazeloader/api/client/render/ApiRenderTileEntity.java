package com.blazeloader.api.client.render;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class ApiRenderTileEntity {
	public static boolean hasSpecialRenderer(Class<? extends TileEntity> clazz) {
		return getSpecialRenderer(clazz) != null;
	}
	
	public static <T extends TileEntity> boolean hasSpecialRenderer(T tileEntity) {
		return TileEntityRendererDispatcher.instance.hasSpecialRenderer(tileEntity);
	}
	
	public static TileEntitySpecialRenderer getSpecialRenderer(Class<? extends TileEntity> clazz) {
		return TileEntityRendererDispatcher.instance.getSpecialRendererByClass(clazz);
	}
	
	public static <T extends TileEntity> TileEntitySpecialRenderer getSpecialRenderer(T tileEntity) {
		return TileEntityRendererDispatcher.instance.getSpecialRenderer(tileEntity);
	}
	
	public static void registerSpecialRenderer(Class<? extends TileEntity> clazz, TileEntitySpecialRenderer renderer) {
		TileEntityRendererDispatcher.instance.mapSpecialRenderers.put(clazz, renderer);
	}
	
	public static void renderTileEntity(TileEntity tile, double x, double y, double z, float partialTicks) {
		TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, x, y, z, partialTicks);
	}
}
