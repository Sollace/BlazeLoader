package com.blazeloader.api.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

public final class EntitySelection<T extends net.minecraft.world.World> extends World<T>.Entities {
	protected EntitySelection(World<T> w) {
		w.super(w);
	}
			
	/**
	 * Counts the number of entities that match the given entity type
	 * @param entityType	The type of entity to filter by
	 * @return Total entities of type N
	 */
	public int count(Class<? extends Entity> entityType) {
		return worldObj.entities.count(entityType);
	}
	
	/**
	 * Gets a list of all entities of a given type
	 * 
	 * @param entityType	The type of entity to filter by
	 * @return All matched entities
	 */
	public <E extends Entity> List<E> get(Class<E> entityType) {
		ArrayList result = Lists.newArrayList();
        for (Entity i : worldObj.entities.getLoadedEntityList()) {
        	if (entityType.isAssignableFrom(i.getClass())) {
        		result.add(i);
        	}
        }
        return result;
	}
	
	public List<Entity> get(AxisAlignedBB boundingBox) {
		return get(Entity.class, boundingBox);
	}
	
	/**
	 * Gets a list of entities within the boundingbox region.
	 * 
	 * @param entityType	The type of entity to filter by
	 * @param boundingBox	The region to look in
	 * @return All matched entities
	 */
	public <E extends Entity> List<E> get(Class<E> entityType, AxisAlignedBB boundingBox) {
		return get(entityType, boundingBox, IEntitySelector.NOT_SPECTATING);
	}
	
	/**
	 * Gets a list of entities within the boundingbox region and that match a given predicate.
	 * 
	 * @param entityType	The type of entity to filter by
	 * @param boundingBox	The region to look in
	 * @param predicate		A predicate used to decide if an entity must be included
	 * @return All matched entities
	 */
	public <E extends Entity> List<E> get(Class<E> entityType, AxisAlignedBB boundingBox, Predicate predicate) {
		ArrayList result = Lists.newArrayList();
        int minX = MathHelper.floor_double((boundingBox.minX - worldObj.max_entity_size) / 16.0D);
        int maxX = MathHelper.floor_double((boundingBox.maxX + worldObj.max_entity_size) / 16.0D);
        int minY = MathHelper.floor_double((boundingBox.minZ - worldObj.max_entity_size) / 16.0D);
        int maxY = MathHelper.floor_double((boundingBox.maxZ + worldObj.max_entity_size) / 16.0D);
        for (int chunkX = minX; chunkX <= maxX; ++chunkX) {
            for (int chunkZ = minY; chunkZ <= maxY; ++chunkZ) {
                if (worldObj.storage.isChunkLoaded(chunkX, chunkZ, true)) {
                    worldObj.storage.getChunkFromChunkCoords(chunkX, chunkZ).getEntitiesOfTypeWithinAAAB(entityType, boundingBox, result, predicate);
                }
            }
        }
        return result;
	}
	
	/**
	 * Gets a list of all entities of the given type and that matches a given predicate.
	 * 
	 * @param entityType	The type of entity to filter by
	 * @param filter		A predicate used to decide if an entity must be included
	 * @return All matched entities
	 */
	public <E extends Entity>  List<E> get(Class<E> entityType, Predicate filter) {
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
		return getExcluding(entity, boundingBox, IEntitySelector.NOT_SPECTATING);
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
		ArrayList result = Lists.newArrayList();
        int minX = MathHelper.floor_double((boundingBox.minX - worldObj.max_entity_size) / 16.0D);
        int maxX = MathHelper.floor_double((boundingBox.maxX + worldObj.max_entity_size) / 16.0D);
        int minY = MathHelper.floor_double((boundingBox.minZ - worldObj.max_entity_size) / 16.0D);
        int maxY = MathHelper.floor_double((boundingBox.maxZ + worldObj.max_entity_size) / 16.0D);
        for (int chunkX = minX; chunkX <= maxX; ++chunkX) {
            for (int chunkZ = minY; chunkZ <= maxY; ++chunkZ) {
                if (worldObj.storage.isChunkLoaded(chunkX, chunkZ, true)) {
                    worldObj.storage.getChunkFromChunkCoords(chunkX, chunkZ).getEntitiesWithinAABBForEntity(entity, boundingBox, result, predicate);
                }
            }
        }
        return result;
	}
	
	/**
	 * Gets the nearest entity to the given entity within the boundingbox region and that matches a given predicate.
	 * 
	 * @param entityType	The type of entity to filter by
	 * @param boundingBox	The region to look in
	 * @param filter		A predicate used to decide if an entity must be included
	 * @param closestTo		An entity that it must be close to
	 * @return All matched entities
	 */
	public <E extends Entity> E getClosest(Class<E> entityType, AxisAlignedBB boundingBox, Entity closestTo) {
		List<E> matched = get(entityType, boundingBox);
        E result = null;
        double lastDistance = Double.MAX_VALUE;
        for (E i : matched) {
            if (i != closestTo && IEntitySelector.NOT_SPECTATING.apply(i)) {
                double distance = closestTo.getDistanceSqToEntity(i);
                if (distance <= lastDistance) {
                    result = i;
                    lastDistance = distance;
                }
            }
        }
        return result;
	}
	
	/**
	 * Gets all entities in the world within a specified distance.
	 * 
	 * @param pos		The origin to look from
	 * @param distance	The maximum distance to search up to
	 * @return A list of all matched entities
	 */
	public List<Entity> getNear(BlockPos pos, double distance) {
		return ApiWorld.getEntitiesNear(worldObj.unwrap(), pos.getX(), pos.getY(), pos.getZ(), distance);
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
		return (List<E>)ApiWorld.getEntitiesOfTypeNear(worldObj.unwrap(), entityType, pos.getX(), pos.getY(), pos.getZ(), distance);
	}
	
	/**
	 * Narrows the selection to the given boundingbox.
	 * 
	 * @param boundingBox	The region to look in
	 * @return A filtered view of this world's entities
	 */
	public BoundedEntitySelection select(AxisAlignedBB boundingBox) {
		return worldObj.boundedEntitySelection.select(boundingBox);
	}
	
	/**
	 * Narrows the selection to the given type of entity.
	 * 
	 * @param entityType	The type of entity to filter by
	 * @return A filtered view of this world's entities
	 */
	public <F extends Entity> ClassEntitySelection<T,F> select(Class<F> entityType) {
		return worldObj.classEntitySelection.select(entityType);
	}
	
	/**
	 * Narrows the selection to the given boundingbox area and type of entity.
	 * 
	 * @param boundingBox	The region to look in
	 * @param entityType	The type of entity to filter by
	 * @return A filtered view of this world's entities
	 */
	public <F extends Entity> ClassBoundedEntitySelection<T,F> select(AxisAlignedBB boundingBox, Class<F> entityType) {
		return worldObj.classBoundedEntitySelection.select(boundingBox, entityType);
	}
}