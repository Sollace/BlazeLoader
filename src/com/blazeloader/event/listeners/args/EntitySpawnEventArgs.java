package com.blazeloader.event.listeners.args;

import net.minecraft.entity.Entity;

public class EntitySpawnEventArgs extends Cancellable {
	private boolean forced = false;
	private boolean entityChanged = false;
	private Entity entity;
	
	public EntitySpawnEventArgs(Entity e) {
		entity = e;
	}
	
	public boolean getForced() {
		return forced;
	}
	
	public void setForced(boolean force) {
		forced = force;
	}
	
	public boolean getEntityChanged() {
		return entityChanged;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity e) {
		entityChanged = true;
		entity = e;
	}
}