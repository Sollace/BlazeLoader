package com.blazeloader.api.entity.tracker;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;

/**
 * Tracker for mods to add special for when their entity is added to the EntityTracker.
 * 
 * @param <T> The type of entity this tracker is handling.
 */
public interface ITrack<T extends Entity> {
	/**
	 * Called to perform any needed checks and to add the entity to the EntityTracker when done.
	 * 
	 * @param entityTracker	EntityTracker to add to
	 * @param entity		The entity we want to track
	 */
	public void addEntityToTracker(EntityTracker entityTracker, T entity);
}
