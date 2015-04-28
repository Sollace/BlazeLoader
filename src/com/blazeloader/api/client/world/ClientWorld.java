package com.blazeloader.api.client.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

import com.blazeloader.api.world.World;

public final class ClientWorld extends World<WorldClient> {
	protected static final World<WorldClient> instance = new ClientWorld();
	
	/**
	 * Gets a wrapped instance of the Minecraft.theWorld.
	 */
	public static World<WorldClient> getClientWorld() {
		return ClientWorld.instance;
	}
	
	private ClientWorld() {}
	
	@Override
	public final WorldClient unwrap() {
		return Minecraft.getMinecraft().theWorld;
	}
}
