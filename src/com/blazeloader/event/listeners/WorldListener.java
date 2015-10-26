package com.blazeloader.event.listeners;

import com.blazeloader.bl.mod.BLMod;

import net.minecraft.world.WorldServer;

/**
 * Server-side world events
 */
public interface WorldListener extends BLMod {
    
    /**
     * Called when a world is loaded
     * @param world	The world being loaded
     */
    public void onWorldInit(WorldServer world);
}
