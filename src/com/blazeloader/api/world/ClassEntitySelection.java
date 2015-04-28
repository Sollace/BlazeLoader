package com.blazeloader.api.world;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import com.google.common.base.Predicate;

public final class ClassEntitySelection<T extends net.minecraft.world.World, E extends Entity> extends World<T>.Entities {
	private Class<E> entityType;
	
	protected ClassEntitySelection(World<T> w) {
		w.super(w);
	}
	
	/**
	 * Counts the number of entities that match the given entity type
	 * @return Total entities of type N
	 */
	public int count() {
		return worldObj.entities.count(entityType);
	}
	
	/**
	 * Gets a list of all matched entities.
	 */
	public List<E> get() {
        return worldObj.entities.get(entityType);
	}
	
	/**
	 * Gets a list of entities within the boundingbox region.
	 * 
	 * @param boundingBox	The region to look in
	 * @return All matched entities
	 */
	public List<E> get(AxisAlignedBB boundingBox) {
		return worldObj.entities.get(entityType, boundingBox);
	}
	
	/**
	 * Gets a list of entities within the boundingbox region and that match a given predicate.
	 * 
	 * @param boundingBox	The region to look in
	 * @param filter		A predicate used to decide if an entity must be included
	 * @return All matched entities
	 */
	public List<E> get(AxisAlignedBB boundingBox, Predicate filter) {
		return worldObj.entities.get(entityType, boundingBox, filter);
	}
	
	/**
	 * Gets a list of all entities of the given type and that matches a given predicate.
	 * 
	 * @param filter		A predicate used to decide if an entity must be included
	 * @return All matched entities
	 */
	public List<E> get(Predicate filter) {
		return worldObj.entities.get(entityType, filter);
	}
	
	/**
	 * Gets a list of all entities within the give boundingbox area that are not the one specified
	 * 
	 * @param entity		The entity to exclude
	 * @param boundingBox	The region to look in
	 * @return All matched entities
	 */
	public List<Entity> getExcluding(Entity entity, AxisAlignedBB boundingBox) {
		return worldObj.entities.getExcluding(entity, boundingBox);
	}
	
	/**
	 * Gets a list of all entities within the give boundingbox area that are not the one specified and that matches a given predicate.
	 * 
	 * @param entity		The entity to exclude
	 * @param boundingBox	The region to look in
	 * @param filter		A predicate used to decide if an entity must be included
	 * @return All matched entities
	 */
	public List<Entity> getExcluding(Entity entity, AxisAlignedBB boundingBox, Predicate predicate) {
		return worldObj.entities.getExcluding(entity, boundingBox, predicate);
	}
	
	/**
	 * Gets the nearest entity to the given entity within the boundingbox region and that matches a given predicate.
	 * 
	 * @param boundingBox	The region to look in
	 * @param filter		A predicate used to decide if an entity must be included
	 * @param closestTo		An entity that it must be close to
	 * @return All matched entities
	 */
	public E getClosest(AxisAlignedBB boundingBox, Entity closestTo) {
		return (E)worldObj.entities.getClosest(entityType, boundingBox, closestTo);
	}
	
	/**
	 * Gets all entities of a given type in the world within a specified distance.
	 * 
	 * @param pos			The origin to look from
	 * @param distance		The maximum distance to search up to
	 * @return A list of all matched entities
	 */
	public List<E> getNear(BlockPos pos, double distance) {
		return worldObj.entities.getNear(entityType, pos, distance);
	}
	
	/**
	 * Changes the entity selection type to the one given.
	 * 
	 * @param entityType	The type of entity to filter by
	 * @return A filtered view of this world's entities
	 */
	public <F extends Entity> ClassEntitySelection<T,F> select(Class<F> clazz) {
		entityType = (Class<E>)clazz;
		return (ClassEntitySelection<T,F>)this;
	}
	
	/**
	 * Narrows the selection to the given boundingbox area and type of entity.
	 * 
	 * @param boundingBox	The region to look in
	 * @return A filtered view of this world's entities
	 */
	public ClassBoundedEntitySelection<T,E> select(AxisAlignedBB boundingBox) {
		return worldObj.classBoundedEntitySelection.select(boundingBox, entityType);
	}
	
	/**
	 * Widens the selection to allow any entity type.
	 */
	public EntitySelection freeEntityType() {
		return worldObj.entities;
	}
}