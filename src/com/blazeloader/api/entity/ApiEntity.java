package com.blazeloader.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

import java.util.Iterator;
import java.util.List;

import com.blazeloader.api.entity.tracker.EntityTrackerRegistry;
import com.blazeloader.api.entity.tracker.ITrack;

/**
 * Api for entity-related functions
 */
public class ApiEntity {

    private static int currFreeEntityId = 1;

    /**
     * Registers a custom entity type.
     *
     * @param entityClass The entity class to register.
     * @param entityName  The name of the entity to register.
     * @param entityId    The entityId that is used to represent the entity over the network and in saves.
     */
    public static void registerEntityType(Class<? extends Entity> entityClass, String entityName, int entityId) {
        EntityList.addMapping(entityClass, entityName, entityId);
    }
    
    /**
     * Registers or replaces a TileEntity
     *
     * @param clazz Tile entity class
     * @param name  Entity name. Used as its id.
     */
    public static void registerTileEntity(Class<? extends TileEntity> clazz, String name) {
        TileEntity.classToNameMap.put(clazz, name);
        TileEntity.nameToClassMap.put(name, clazz);
    }
    
    /**
     * Registers a spawn egg for a given entity type.
     *
     * @param entityId The entityID for this egg.
     * @param eggInfo  The EntityEggInfo to register.
     */
    public static void registerEntityEggInfo(int entityId, EntityList.EntityEggInfo eggInfo) {
        EntityList.entityEggs.put(entityId, eggInfo);
    }
    
    /**
     * Registers an entity to be tracked by minecraft's EntityTracker.
     * 
     * @param entityClass		Type of entity to track
     * @param range				Maximum range to which the entity will be tracked
     * @param updateFrequency	How often position/velocity updates are sent
     * @param includeVelocity	True to send velocity
     */
    public static void registerEntityTracker(Class<? extends Entity> entityClass, int range, int updateFrequency, boolean includeVelocity) {
    	EntityTrackerRegistry.instance().addTracker(entityClass, range, updateFrequency, includeVelocity);
	}
    
    /**
     * Registers an entity type with a custom tracker for handling when the entity must be added to the vanilla EntityTracker.
     * 
     * @param entityClass	Type of entity to track
     * @param entityTracker	The tracker to handle the entity
     */
    public static <T extends Entity> void registerEntityTracker(Class<T> entityClass, ITrack<T> entityTracker) {
    	EntityTrackerRegistry.instance().addTracker(entityClass, entityTracker);
    }
    
    /**
     * Changes the class used by an entity universally.
     *
     * @param o Original class
     * @param c Replacement class
     */
    public static void swapEntityClass(Class<? extends Entity> o, Class<? extends Entity> c) {
        if (EntityList.classToStringMapping.containsKey(o) && EntityList.classToIDMapping.containsKey(c)) {
            String name = (String) EntityList.classToStringMapping.get(o);
            int id = (Integer) EntityList.classToIDMapping.get(o);

            EntityList.stringToClassMapping.put(name, c);
            EntityList.classToStringMapping.put(c, name);
            EntityList.idToClassMapping.put(id, c);
            EntityList.classToIDMapping.put(c, id);

            for (EnumCreatureType i : EnumCreatureType.values()) {
                ApiEntity.swapEntitySpawn(o, c, i);
            }
        }
    }

    /**
     * Replaces the class used when spawning an entity in a world.
     *
     * @param o Original class
     * @param c Replacement class
     * @param e CreatureType
     */
    public static void swapEntitySpawn(Class<? extends Entity> o, Class<? extends Entity> c, EnumCreatureType e) {
        BiomeGenBase[] standardBiomes = BiomeGenBase.getBiomeGenArray();

        for (BiomeGenBase biome : standardBiomes) {
            if (biome != null) {
                List<SpawnListEntry> spawnableList = biome.getSpawnableList(e);
                if (spawnableList != null) {
                    for (SpawnListEntry entry : spawnableList) {
                        if (entry != null && entry.entityClass == o) {
                            entry.entityClass = c;
                        }
                    }
                }
            }
        }
    }

    /**
     * Registers an Entity spawn in certain biomes.
     * Registers for all biomes if none specified
     *
     * @param c        Entity class
     * @param weight   How often this entity is spawned in the world
     * @param minGroup Minimum group size
     * @param maxGroup Maximum group size
     * @param type     Type of spwning to be used by this entity
     * @param biomes   List of biomes this entity should spawn in
     */
    public static void registerSpawn(Class<? extends Entity> c, int weight, int minGroup, int maxGroup, EnumCreatureType type, BiomeGenBase... biomes) {
        if (biomes.length == 0) {
            biomes = BiomeGenBase.getBiomeGenArray();
        }

        for (BiomeGenBase biome : biomes) {
            if (biome != null) {
                List<SpawnListEntry> spawnableList = biome.getSpawnableList(type);
                Iterator<SpawnListEntry> iter = spawnableList.iterator();
                while (iter.hasNext()) {
                    if (iter.next().entityClass == c) iter.remove();  //Ensure there are no duplications
                }
                spawnableList.add(new SpawnListEntry(c, weight, minGroup, maxGroup));
            }
        }
    }
    
    public static boolean isIdFree(int id) {
    	return getEntityClassFromID(id) == null;
    }
    
    /**
     * Gets a free entity ID.
     *
     * @return return a free entity ID.
     */
    public static int getFreeEntityId() {
        while (!isIdFree(currFreeEntityId)) {
            currFreeEntityId++;
        }
        return currFreeEntityId++;
    }

    /**
     * Gets the entity ID of the passed entity.
     *
     * @param entity The entity to get the ID from
     * @return Return the ID of the entity
     */
    public static int getEntityID(Entity entity) {
        return EntityList.getEntityID(entity);
    }
    
    /**
     * Gets the string id for the given TileEntity
     * 
     * @param entity	The tile entity
     * @return the String ID for the tile entity
     */
    public static String getTileEntityID(TileEntity entity) {
    	return (String)TileEntity.classToNameMap.get(entity.getClass());
    }

    /**
     * Gets an entity's class from an entity ID
     *
     * @param id The ID of the entity.
     * @return Return the class of the passed entity.
     */
    public static Class<? extends Entity> getEntityClassFromID(int id) {
        return EntityList.getClassFromID(id);
    }

    /**
     * Gets the type of an entity
     *
     * @param entity The entity who's type to get
     * @return Return the type of the passed entity.
     */
    public static String getEntityType(Entity entity) {
        return EntityList.getEntityString(entity);
    }

    /**
     * Gets the type of an entity from it's ID
     *
     * @param id The ID of the entity.
     * @return Return the type of the entity.
     */
    public static String getEntityTypeFromID(int id) {
        return EntityList.getStringFromID(id);
    }

    /**
     * Gets an entity ID from a String.
     *
     * @param type The string identifying the entity.
     * @return Return then ID of the entity.
     */
    public static int getEntityIDFromType(String type) {
        return (Integer) EntityList.stringToIDMapping.get(type);
    }

    /**
     * Creates an Entity from it's entity ID
     *
     * @param id    The ID of the entity
     * @param world The world to spawn in
     * @return Return the spawned entity.
     */
    public static Entity createEntityByID(int id, World world) {
        return EntityList.createEntityByID(id, world);
    }

    /**
     * Creates an Entity from an NBT structure
     *
     * @param nbt   The NBT to load from
     * @param world The world to spawn in
     * @return Return the spawned entity.
     */
    public static Entity createEntityFromNBT(NBTTagCompound nbt, World world) {
        return EntityList.createEntityFromNBT(nbt, world);
    }

    /**
     * Creates an Entity from the specified type
     *
     * @param type  The type of entity to spawn
     * @param world The world to spawn in
     * @return Return the spawned entity.
     */
    public static Entity createEntityByType(String type, World world) {
        return EntityList.createEntityByName(type, world);
    }
}
