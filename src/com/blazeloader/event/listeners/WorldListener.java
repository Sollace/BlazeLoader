package com.blazeloader.event.listeners;

import com.blazeloader.bl.mod.BLMod;

import net.minecraft.entity.Entity;
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
     * Called when an entity is added to a world
     * 
     * @param world		The world
     * @param entity	The entity being added
     */
    public void onEntitySpawned(World world, Entity entity);
}
