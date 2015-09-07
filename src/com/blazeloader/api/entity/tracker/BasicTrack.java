package com.blazeloader.api.entity.tracker;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;

public class BasicTrack<T extends Entity> implements ITrack {
	
	private final int range;
	private final int frequency;
	private final boolean velocity;
	
	protected BasicTrack(int range, int frequency, boolean includeVelocity) {
		this.range = range;
		this.frequency = frequency;
		this.velocity = includeVelocity;
	}
	
	public void addEntityToTracker(EntityTracker entityTracker, Entity entity) {
		entityTracker.addEntityToTracker(entity, range, frequency, velocity);
	}
}