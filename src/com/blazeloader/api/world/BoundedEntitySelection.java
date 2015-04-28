package com.blazeloader.api.world;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import com.google.common.base.Predicate;

public final class BoundedEntitySelection<T extends net.minecraft.world.World> extends World<T>.Entities {
	private AxisAlignedBB boundingBox;
	
	protected BoundedEntitySelection(World<T> w) {
		w.super(w);
	}
	
	/**
	 * Counts the number of entities that match the given entity type
	 * @param entityType	The type of entity to filter by
	 * @return Total entities of type N
	 */
	public int count(Class<? extends Entity> entityType) {
		return get(entityType).size();
	}
	
	/**
	 * Gets a list of all matched entities.
	 */
	public List<Entity> get() {
		return worldObj.entities.get(boundingBox);
	}
	
	/**
	 * Gets a list of entities within the boundingbox region.
	 * 
	 * @param entityType	The type of entity to filter by
	 * @return All matched entities
	 */
	public <E extends Entity> List<E> get(Class<E> entityType) {
		return worldObj.entities.get(entityType, boundingBox);
	}
	
	/**
	 * Gets a list of entities within the boundingbox region and that match a given predicate.
	 * 
	 * @param entityType	The type of entity to filter by
	 * @param filter		A predicate used to decide if an entity must be included
	 * @return All matched entities
	 */
	public <E extends Entity> List<E> get(Class<E> entityType, Predicate filter) {
		return worldObj.entities.get(entityType, boundingBox, filter);
	}
	
	/**
	 * Gets a list of all entities within the give boundingbox area that are not the one specified
	 * 
	 * @param entity		The entity to exclude
	 * @return All matched entities
	 */
	public List<Entity> getExcluding(Entity entity) {
		return worldObj.entities.getExcluding(entity, boundingBox);
	}
	
	/**
	 * Gets a list of all entities within the give boundingbox area that are not the one specified and that matches a given predicate.
	 * 
	 * @param entity		The entity to exclude
	 * @param filter		A predicate used to decide if an entity must be included
	 * @return All matched entities
	 */
	public List<Entity> getExcluding(Entity entity, Predicate predicate) {
		return worldObj.entities.getExcluding(entity, boundingBox, predicate);
	}
	
	/**
	 * Gets the nearest entity to the given entity within the boundingbox region and that matches a given predicate.
	 * 
	 * @param entityType	The type of entity to filter by
	 * @param filter		A predicate used to decide if an entity must be included
	 * @param closestTo		An entity that it must be close to
	 * @return All matched entities
	 */
	public <E extends Entity> E getClosest(Class<E> entityType, Entity closestTo) {
		return (E)worldObj.entities.getClosest(entityType, boundingBox, closestTo);
	}
	
	/**
	 * Gets all entities in the world within a specified distance.
	 * 
	 * @param pos		The origin to look from
	 * @param distance	The maximum distance to search up to
	 * @return A list of all matched entities
	 */
	public List<Entity> getNear(BlockPos pos, double distance) {
		return worldObj.entities.getNear(pos, distance);
	}
	
	/**
	 * Gets all entities of a given type in the world within a specified distance.
	 * 
	 * @param entityType	The type of entity to filter by
	 * @param pos			The origin to look from
	 * @param distance		The maximum distance to search up to
	 * @return A list of all matched entities
	 */
	public <E extends Entity> List<E> getNear(Class<E> entityType, BlockPos pos, double distance) {
		return worldObj.entities.getNear(entityType, pos, distance);
	}
	
	/**
	 * Changes the boundingbox selection type to the one given.
	 * 
	 * @param bounds	The area to look in
	 * @return A filtered view of this world's entities
	 */
	public BoundedEntitySelection select(AxisAlignedBB bounds) {
		boundingBox = bounds;
		return this;
	}
	
	/**
	 * Narrows the selection to the given boundingbox area and type of entity.
	 * 
	 * @param entityType	The type of entity to filter by
	 * @return A filtered view of this world's entities
	 */
	public ClassBoundedEntitySelection select(Class<? extends Entity> entityType) {
		return worldObj.classBoundedEntitySelection.select(boundingBox, entityType);
	}
	
	/**
	 * Widens the selection to allow entities anywhere in the world.
	 */
	public EntitySelection freeBounds() {
		return worldObj.entities;
	}
}