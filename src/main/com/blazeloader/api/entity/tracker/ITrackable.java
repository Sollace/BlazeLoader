package com.blazeloader.api.entity.tracker;

/**
 * An entity that must be added to the EntityTracker when added to the world.
 * <p>
 * Mods can implement this interface on their entity class instead of having to register it explicitly.
 */
public interface ITrackable {
	/**
	 * Gets the maximum range to which this entity can be tracked.
	 */
	public int getMaxRange();
	
	/**
	 * Gets the frequency that updates will be sent for this entity.
	 */
	public int getUpdateFrequency();
	
	/**
	 * true if the entity must send velocity updates.
	 */
	public boolean mustSendVelocity();
}
