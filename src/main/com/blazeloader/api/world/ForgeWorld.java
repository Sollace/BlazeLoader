package com.blazeloader.api.world;

import net.minecraft.world.World;

import com.blazeloader.bl.interop.ForgeWorldAccess;

/**
 * Reflection based access to world methods added by forge.
 */
public final class ForgeWorld {
	
    /**
     * Gets a direct accessor to Forge methods on the given Minecraft world.
     * This may be used in cases where a mod specifically requires access
     * to forge's methods without invoking BlazeLoader's implementations.
     * 
     * @param w		A world instance to apply to
     */
    public static ForgeWorldAccess getForgeWorld(World w) {
    	return (ForgeWorldAccess)w;
    }
}
