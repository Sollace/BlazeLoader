package com.blazeloader.api.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.WorldServer;

import com.blazeloader.api.client.world.ClientWorld;

/**
 * A provider for wrapped worlds.
 */
public class WorldPool {
	private static final List<ServerWorld> unusedPool = new ArrayList<ServerWorld>();
	private static final List<ServerWorld> usedPool = new ArrayList<ServerWorld>();
	
	private static int maximum_idle_worlds = 5;
	
	/**
	 * Retrieves a World from the pool of possible world objects corresponding to the given Minecraft world.
	 * 
	 * @param world	A minecraft world
	 * 
	 * @return	A world to be used to access the given minecraft world
	 */
	public static <T extends net.minecraft.world.World> World<T> unpool(T world) {
		if (world != null) {
			if (world instanceof WorldServer) {
				return (World<T>)getServerWorld((WorldServer)world);
			} else {
				return (World<T>)ClientWorld.getClientWorld();
			}
			
		}
		return null;
	}
	
	private static <T extends net.minecraft.world.WorldServer> World<T> getServerWorld(T world) {
		ServerWorld result = null;
		trimWorlds();
		if (usedPool.size() > 0) {
			for (ServerWorld w : usedPool) {
				if (w.worldObj == world) {
					result = w;
					break;
				}
			}
		}
		if (result == null) {
			if (unusedPool.size() > 0) {
				result = unusedPool.remove(0);
				result.checkoutNumber = 0;
				result.worldObj = world;
				result.entities.getMaxEntitySize();
			}
			if (result == null) {
				result = new ServerWorld(world);
				result.entities.getMaxEntitySize();
			}
		}
		result.checkoutNumber++;
		if (unusedPool.contains(result)) {
			unusedPool.remove(result);
		}
		if (!usedPool.contains(result)) {
			usedPool.add(result);
		}
		return result;
	}
	
	public static void flushWorld(WorldServer world) {
		for (ServerWorld i : usedPool) {
			if (i.equals(world)) {
				i.checkoutNumber = 0;
				i.worldObj = null;
				if (unusedPool.size() < getMaxIdle()) {
					unusedPool.add(i);
				}
				usedPool.remove(i);
				return;
			}
		}
	}
	
	/**
	 * Returns a world to the pool. It is recommended that you call this after you are done with this world.
	 * <p>
	 * If you keep it, it may turn into a different world.
	 * 
	 * @param w	The world to recycle
	 */
	public static void pool(World world) {
		if (world != null && world.isServer()) {
			ServerWorld w = (ServerWorld)world;
			if (w.checkoutNumber > 0) w.checkoutNumber--;
			if (w.checkoutNumber <= 0) {
				w.worldObj = null;
				if (!unusedPool.contains(w) && unusedPool.size() < getMaxIdle()) {
					unusedPool.add(w);
				}
			}
			if (usedPool.contains(w)) {
				usedPool.remove(w);
			}
		}
	}
	
	/**
	 * Gets the total number of world instances being tracked.
	 */
	public static int totalInstances() {
		return totalInstancesInUse() + unusedPool.size();
	}
	
	/**
	 * Gets the total number of world instances currently being used by the game.
	 */
	public static int totalInstancesInUse() {
		return usedPool.size();
	}
	
	/**
	 * Gets the total number of world instances currently not in use.
	 * @return
	 */
	public static int totalIdleInstances() {
		return unusedPool.size();
	}
	
	/**
	 * Gets the number of worlds allowed to wait in the pool for someone to use them.
	 * <p>
	 * Default: 5
	 */
	public static int getMaxIdle() {
		return maximum_idle_worlds;
	}
	
	/**
	 * Increase this number to allow more Worlds hanging idle. May improve performance but at the cost of more memory.
	 */
	public static void setMaxIdle(int max) {
		maximum_idle_worlds = max;
		trimWorlds();
	}
	
	private static void trimWorlds() {
		while (unusedPool.size() > getMaxIdle()) {
			unusedPool.remove(0);
		}
	}
}
