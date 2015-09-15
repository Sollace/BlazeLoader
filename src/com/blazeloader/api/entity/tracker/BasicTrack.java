package com.blazeloader.api.entity.tracker;

import com.blazeloader.bl.main.BLPacketChannels;
import com.blazeloader.bl.network.BLPacketSpawnObject;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.network.Packet;

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
	
	public Packet getEntitySpawnPacket(EntityTrackerEntry entry) {
		//func_151260_c()
        return BLPacketChannels.instance().getRawPacket(new BLPacketSpawnObject.Message(entry.trackedEntity, 1));
	}
}