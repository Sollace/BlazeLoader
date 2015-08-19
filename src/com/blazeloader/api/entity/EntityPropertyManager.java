package com.blazeloader.api.entity;

import java.util.HashMap;
import java.util.UUID;

import com.blazeloader.bl.main.BLMain;
import com.mumfrey.liteloader.core.event.HandlerList;

import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * This can probably be replaced with storing the properties in the entity class directly.
 */
public class EntityPropertyManager {
	
	private static final HashMap<UUID, Properties> mapping = new HashMap<UUID, Properties>();
	
	public static void registerEntityProperties(Entity e, IEntityProperties p) {
		if (e != null) {
			Properties properties = null;
			UUID key = e.getUniqueID();
			if (!mapping.containsKey(key)) {
				properties = new Properties();
				mapping.put(key, properties);
			} else {
				properties = mapping.get(key);
			}
			properties.registerHandler(p);
		}
	}
	
	public static void unRegisterEntityProperties(Entity e, IEntityProperties p) {
		UUID key = e.getUniqueID();
		if (mapping.containsKey(key)) {
			mapping.get(key).unRegisterHandler(p);
		}
	}
	
	public static <E extends IEntityProperties> E getEntityPropertyObject(Entity e, Class<E> c) {
		UUID key = e.getUniqueID();
		if (mapping.containsKey(key)) {
			return mapping.get(key).getHandler(e, c);
		}
		return null;
	}
	
	public static void copyToEntity(Entity source, Entity destination) {
		UUID key = source.getUniqueID();
		if (mapping.containsKey(key)) {
			Properties entry = mapping.get(key);
			if (entry != null) {
				if (destination != null) {
					mapping.put(destination.getUniqueID(), entry);
				}
			}
		}
	}
	
	public static void entityDestroyed(Entity e) {
		mapping.remove(e.getUniqueID());
	}
	
	public static void entityinit(Entity e) {
		UUID key = e.getUniqueID();
		if (mapping.containsKey(key)) {
			mapping.get(key).entityInit(e, e.worldObj);
		}
	}
	
	public static void onEntityUpdate(Entity e) {
		UUID key = e.getUniqueID();
		if (mapping.containsKey(key)) {
			mapping.get(key).onEntityUpdate(e);
		}
	}
	
	public static void readFromNBT(Entity e, NBTTagCompound t) {
		UUID key = e.getUniqueID();
		if (mapping.containsKey(key)) {
			//We have to make sure the registry's object remains with the entity after it's UUID is loaded from NBT
			UUID newKey = null;
	        if (t.hasKey("UUIDMost", 4) && t.hasKey("UUIDLeast", 4)) {
	            newKey = new UUID(t.getLong("UUIDMost"), t.getLong("UUIDLeast"));
	        } else if (t.hasKey("UUID", 8)) {
	            newKey = UUID.fromString(t.getString("UUID"));
	        }
	        if (newKey == null) newKey = key;
			NBTTagCompound modsTag;
			if (t.hasKey("BlazeLoader")) {
				modsTag = t.getCompoundTag("BlazeLoader");
			} else {
				modsTag = new NBTTagCompound();
			}
			Properties p = mapping.remove(key);
			try {
				p.readFromNBT(modsTag);
			} catch (Throwable er) {
				BLMain.LOGGER_MAIN.logFatal("Failed in reading entity NBT into (" + p.getClass().getCanonicalName() + ").", er);
			}
			mapping.put(newKey, p);
		}
	}
	
	public static void writeToNBT(Entity e, NBTTagCompound t) {
		UUID key = e.getUniqueID();
		if (mapping.containsKey(key)) {
			NBTTagCompound modsTag = new NBTTagCompound();
			t.setTag("BlazeLoader", modsTag);
			Properties p = mapping.get(key);
			try {
				p.writeToNBT(modsTag);
			} catch (Throwable er) {
				BLMain.LOGGER_MAIN.logFatal("Failed in writing entity NBT from (" + p.getClass().getCanonicalName() + ").", er);
			}
		}
	}
	
	public static void addEntityCrashInfo(Entity e, CrashReportCategory section) {
		UUID key = e.getUniqueID();
		if (mapping.containsKey(key)) {
			Properties p = mapping.get(key);
			try {
				section.addCrashSection("BlazeLoader Extended Entity Properties", p.getCrashInfo());
			} catch (Throwable er) {
				section.addCrashSectionThrowable("BLMod error", er);
			}
		}
	}
	
	private static class Properties {
		private final HandlerList<IEntityProperties> handlers = new HandlerList<IEntityProperties>(IEntityProperties.class);
		
		private Properties() {}
		
		public void registerHandler(IEntityProperties handler) {
			for (IEntityProperties i : handlers) {
				if (i.getClass().equals(handler.getClass())) {
					BLMain.LOGGER_MAIN.logWarning("Attempted to register duplicate Properties object (" + handler.getClass().getCanonicalName() + "). Only one instance allowed per entity. Handler was not registered.");
					return;
				}
			}
			handlers.add(handler);
		}
		
		public <E extends IEntityProperties> E getHandler(Entity e, Class<E> c) {
			try {
				if (handlers != null) {
					for (IEntityProperties i : handlers) {
						if (i.getClass() == c) {
							E result = (E)i;
							result.setOwningEntity(e);
							return result;
						}
					}
				}
			} catch (Throwable t) {
				BLMain.LOGGER_MAIN.logError("Unexpected error whilst retrieving entity properties: ", t);
			}
			return null;
		}
		
		public void unRegisterHandler(IEntityProperties handler) {
			if (handlers.contains(handler)) handlers.remove(handler);
		}
		
		public void onEntityUpdate(Entity entity) {
			handlers.all().onEntityUpdate(entity);
		}
		
		public void entityInit(Entity entity, World world) {
			handlers.all().entityInit(entity, world);
		}
		
		public void readFromNBT(NBTTagCompound t) {
			handlers.all().readFromNBT(t);
		}
		
		public void writeToNBT(NBTTagCompound t) {
			handlers.all().writeToNBT(t);
		}
		
		public String getCrashInfo() {
			StringBuilder result = new StringBuilder();
			result.append("Extended Properties Registered: " + handlers.size());
			for (IEntityProperties i : handlers) {
				CrashReportCategory category = new CrashReportCategory(null, "Mod:" + i.getClass().toString());
				i.addEntityCrashInfo(category);
				category.appendToStringBuilder(result);
			}
			return result.toString();
		}
		
		public String toString() {
			return "Properties(" + handlers.size() + ")#" + handlers.toString();
		}
	}
}
