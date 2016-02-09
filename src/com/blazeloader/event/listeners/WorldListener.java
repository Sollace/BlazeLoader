package com.blazeloader.event.listeners;

import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.event.listeners.args.EntitySpawnEventArgs;

import net.minecraft.world.World;
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
    
    /**
     * Called when an entity is spawned in the world.
     * <br>
     * This does not necessarily mean the entity is new, it may be removed and readded.
     * i.e when going through a portal
     * 
     * @param world		The world
     * @param args		Additional information about the current event
     */
    public void onEntitySpawned(World world, EntitySpawnEventArgs args);
}
