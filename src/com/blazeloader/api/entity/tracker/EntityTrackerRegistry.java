package com.blazeloader.api.entity.tracker;

import java.util.HashMap;

import com.blazeloader.bl.main.BLPacketChannels;
import com.blazeloader.bl.network.BLPacketSpawnObject;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.network.Packet;

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
	
	public Packet getSpawnPacket(EntityTrackerEntry trackerEntry) {
		if (trackerEntry != null) {
			if (trackerEntry.trackedEntity instanceof ITrackable) {
				return BLPacketChannels.instance().getRawPacket(new BLPacketSpawnObject.Message(trackerEntry.trackedEntity, 1));
			}
			ITrack entry = mappings.get(trackerEntry.trackedEntity.getClass());
			if (entry != null) {
				return entry.getEntitySpawnPacket(trackerEntry);
			}
		}
		return null;
	}
}
