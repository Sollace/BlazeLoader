package com.blazeloader.event.listeners;

import com.blazeloader.bl.mod.BLMod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.network.play.server.SPacketSpawnObject;

/**
 * Interface for mods that want to get events as an entity is tracked.
 * These will recieve events in the case that neither vanilla minecraft nor the EntityTrackerRegitry knows what to do with an entity. 
 *
 */
public interface EntityTrackingListener extends BLMod {
    /**
     * Creates a spawn packet for the given entity.
     *
     * @param entity    The entity to create the spawn packet for.
     * @param isHandled True if another mod has already created a packet for this entity.
     * @return Return a spawn packet for the given entity, or null if none exists.
     */
    public SPacketSpawnObject onCreateSpawnPacket(Entity entity, boolean isHandled);

    /**
     * Adds an entity to an entity tracker.
     *
     * @param tracker   The tracker to add the entity to.
     * @param entity    The entity to add.
     * @param isHandled True if another mod has already handled the event.
     * @return Return true if the entity was added, false otherwise.
     */
    public boolean onAddEntityToTracker(EntityTracker tracker, Entity entity, boolean isHandled);
}
