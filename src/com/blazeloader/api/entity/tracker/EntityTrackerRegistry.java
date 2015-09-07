package com.blazeloader.api.entity.tracker;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;

public class EntityTrackerRegistry {
	private static EntityTrackerRegistry instance = new EntityTrackerRegistry();
	
	private HashMap<Class<? extends Entity>, ITrack> mappings = new HashMap<Class<? extends Entity>, ITrack>();
	
	public static EntityTrackerRegistry instance() {
		return instance;
	}
	
	public void addTracker(Class entityClass, int range, int updateFrequency, boolean includeVelocity) {
		addTracker(entityClass, new BasicTrack(range, updateFrequency, includeVelocity));
	}
	
	public void addTracker(Class entityClass, ITrack tracker) {
		mappings.put(entityClass, tracker);
	}
	
	public boolean addEntityToTracker(EntityTracker tracker, Entity entity) {
		if (entity != null) {
			if (entity instanceof ITrackable) {
				ITrackable trackable = (ITrackable)entity;
				tracker.addEntityToTracker(entity, trackable.getMaxRange(), trackable.getUpdateFrequency(), trackable.mustSendVelocity());
				return true;
			} else {
				ITrack entry = mappings.get(entity.getClass());
				if (entry != null) {
					entry.addEntityToTracker(tracker, entity);
					return true;
				}
			}
		}
		return false;
	}
}
