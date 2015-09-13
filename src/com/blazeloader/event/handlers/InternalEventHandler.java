package com.blazeloader.event.handlers;

import java.util.List;
import java.util.Random;

import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.command.CommandHandler;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.blazeloader.api.ApiGeneral;
import com.blazeloader.api.entity.properties.EntityPropertyManager;
import com.blazeloader.api.entity.tracker.EntityTrackerRegistry;
import com.blazeloader.api.world.ApiWorld;

import com.blazeloader.api.world.gen.IChunkGenerator;
import com.blazeloader.api.world.gen.UnpopulatedChunksQ;

import com.blazeloader.api.world.WorldPool;

import com.blazeloader.bl.main.BLMain;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;

/**
 * Event handler for events that are not passed to mods, but rather to BL itself
 */
public class InternalEventHandler {
    public static void eventCreateNewCommandManager(ReturnEventInfo<MinecraftServer, CommandHandler> event) {
        event.setReturnValue(BLMain.instance().getCommandHandler());
    }

    public static void eventGetClientModName(ReturnEventInfo<ClientBrandRetriever, String> event) {
		event.setReturnValue(retrieveBrand(event.getReturnValue()));
	}

	public static void eventGetServerModName(ReturnEventInfo<MinecraftServer, String> event) {
		event.setReturnValue(retrieveBrand(event.getReturnValue()));
	}

	private static String retrieveBrand(String inheritedBrand) {
		String brand = ApiGeneral.getBrand();
		if (inheritedBrand != null && !(inheritedBrand.isEmpty() || "vanilla".contentEquals(inheritedBrand) || "LiteLoader".contentEquals(inheritedBrand))) {
			return inheritedBrand + " / " + brand;
		}
		return brand;
	}

	public static void eventPopulateChunk(EventInfo<Chunk> event, IChunkProvider providerOne, IChunkProvider providerTwo, int chunkX, int chunkZ) {
		Chunk chunk = event.getSource();
		if (UnpopulatedChunksQ.instance().pop(chunk)) {
			Random random = new Random(chunk.getWorld().getSeed());
			long seedX = random.nextLong() >> 2 + 1l;
			long seedZ = random.nextLong() >> 2 + 1l;
			long chunkSeed = (seedX * chunk.xPosition + seedZ * chunk.zPosition) ^ chunk.getWorld().getSeed();

			List<IChunkGenerator> generators = ApiWorld.getGenerators();
			for (IChunkGenerator i : generators) {
				random.setSeed(chunkSeed);
				try {
					i.populateChunk(chunk, providerOne, providerTwo, chunkX, chunkZ, random);
				} catch (Throwable e) {
					throw new ReportedException(CrashReport.makeCrashReport(e, "Exception during mod chunk populating"));
				}
			}
		}
	}
	
	public static void eventOnEntityRemoved(EventInfo<World> event, Entity entity) {
		if (entity.isDead) {
			if (!(entity instanceof EntityPlayer)) {
				EntityPropertyManager.entityDestroyed(entity);
			}
		}
	}
	
	public static void eventOnUpdate(EventInfo<Entity> event) {
		EntityPropertyManager.onEntityUpdate(event.getSource());
	}
	
    public static void eventWriteToNBT(EventInfo<Entity> event, NBTTagCompound tag) {
    	EntityPropertyManager.writeToNBT(event.getSource(), tag);
    }
    
    public static void eventReadFromNBT(EventInfo<Entity> event, NBTTagCompound tag) {
    	EntityPropertyManager.readFromNBT(event.getSource(), tag);
    }
    
    public static void eventClonePlayer(EventInfo<EntityPlayer> event, EntityPlayer old, boolean respawnedFromEnd) {
    	EntityPropertyManager.copyToEntity(old, event.getSource());
    	EntityPropertyManager.entityDestroyed(old);
    }
    
    public static void eventCopyDataFromOld(EventInfo<Entity> event, Entity old) {
    	EntityPropertyManager.copyToEntity(old, event.getSource());
    }
    
    public static void eventAddEntityCrashInfo(EventInfo<Entity> event, CrashReportCategory section) {
    	EntityPropertyManager.addEntityCrashInfo(event.getSource(), section);
    }
    
    public static void eventTrackEntity(EventInfo<EntityTracker> event, Entity entity) {
    	if (EntityTrackerRegistry.instance().addEntityToTracker(event.getSource(), entity)) {
    		event.cancel();
    	}
    }
    
    public static void eventFlush(EventInfo<WorldServer> event) {
    	WorldPool.flushWorld(event.getSource());
    }
}
