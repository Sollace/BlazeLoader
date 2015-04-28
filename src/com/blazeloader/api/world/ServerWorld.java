package com.blazeloader.api.world;

import net.minecraft.world.WorldServer;

public final class ServerWorld<T extends net.minecraft.world.World> extends World<T> {
	protected int checkoutNumber = 0;
	protected T worldObj;
	
	/**
	 * Gets a wrapped instance of the server world for the given dimension index.
	 * 
	 * @param dimension		A dimension index.
	 */
	public static World<WorldServer> getServerWorld(int dimension) {
		return WorldPool.unpool(ApiWorld.getServerWorldForDimension(dimension));
	}
	
	protected ServerWorld(T world) {
		worldObj = world;
	}
	
	@Override
	public final T unwrap() {
		return worldObj;
	}
}
