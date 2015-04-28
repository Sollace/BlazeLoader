package com.blazeloader.api.world;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import com.google.common.base.Predicate;

public final class ClassBoundedEntitySelection<T extends net.minecraft.world.World, E extends Entity> extends World<T>.Entities {
	private AxisAlignedBB boundingBox;
	private Class<E> entityType;
	
	protected ClassBoundedEntitySelection(World<T> w) {
		w.super(w);
	}
	
	/**
	 * Counts the number of entities that match the given entity type
	 * 
	 * @return Total entities of type N
	 */
	public int count() {
		return get().size();
	}
	
	/**
	 * Gets a list of all matched entities.
	 */
	public List<E> get() {
		return worldObj.entities.get(entityType, boundingBox);
	}
	
	/**
	 * Gets a list of entities within the boundingbox region and that match a given predicate.
	 * 
	 * @param filter		A predicate used to decide if an entity must be included
	 * @return All matched entities
	 */
	public List<E> get(Predicate filter) {
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
	 * @param filter		A predicate used to decide if an entity must be included
	 * @param closestTo		An entity that it must be close to
	 * @return All matched entities
	 */
	public E getClosest(Entity closestTo) {
		return (E)worldObj.entities.getClosest(entityType, boundingBox, closestTo);
	}
	
	/**
	 * Gets all entities of a given type in the world within a specified distance.
	 * 
	 * @param pos		The origin to look from
	 * @param distance	The maximum distance to search up to
	 * @return A list of all matched entities
	 */
	public List<E> getNear(BlockPos pos, double distance) {
		return worldObj.entities.getNear(entityType, pos, distance);
	}
	
	/**
	 * Changes the selection bounds and entity type to the given parameters.
	 * 
	 * @param bounds	Boundingbox to look inside
	 * @param clazz		The type of entity to filter by
	 * @return A filtered view of this world's entities
	 */
	public <F extends Entity> ClassBoundedEntitySelection<T,F> select(AxisAlignedBB bounds, Class<F> clazz) {
		entityType = (Class<E>)clazz;
		boundingBox = bounds;
		return (ClassBoundedEntitySelection<T,F>)this;
	}
	
	/**
	 * Changes the entity selection type to the one given.
	 * 
	 * @param clazz		The type of entity to filter by
	 * @return A filtered view of this world's entities
	 */
	public <F extends Entity> ClassBoundedEntitySelection<T,F> select(Class<F> clazz) {
		entityType = (Class<E>)clazz;
		return (ClassBoundedEntitySelection<T,F>)this;
	}
	
	/**
	 * Changes the boundingbox selection to the one given.
	 * 
	 * @param bounds	The area to look in
	 * @return A filtered view of this world's entities
	 */
	public ClassBoundedEntitySelection<T,E> select(AxisAlignedBB bounds) {
		boundingBox = bounds;
		return this;
	}
	
	/**
	 * Widens the selection to allow any entity type.
	 */
	public BoundedEntitySelection<T> freeEntityType() {
		return worldObj.boundedEntitySelection.select(boundingBox);
	}
	
	/**
	 * Widens the selection to allow entities anywhere in the world.
	 */
	public ClassEntitySelection<T,E> freeBounds() {
		return worldObj.classEntitySelection.select(entityType);
	}
}