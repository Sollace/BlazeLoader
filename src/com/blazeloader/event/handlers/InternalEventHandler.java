package com.blazeloader.event.handlers;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.command.CommandHandler;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.blazeloader.api.ApiGeneral;
import com.blazeloader.api.block.ISided;
import com.blazeloader.api.entity.properties.EntityPropertyManager;
import com.blazeloader.api.entity.tracker.EntityTrackerRegistry;
import com.blazeloader.api.recipe.FurnaceFuels;
import com.blazeloader.api.world.ApiWorld;
import com.blazeloader.api.world.gen.IChunkGenerator;
import com.blazeloader.api.world.gen.UnpopulatedChunksQ;
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
				EntityPropertyManager.instance().entityDestroyed(entity);
			}
		}
	}
	
	public static void eventOnUpdate(EventInfo<Entity> event) {
		EntityPropertyManager.instance().onEntityUpdate(event.getSource());
	}
	
    public static void eventWriteToNBT(EventInfo<Entity> event, NBTTagCompound tag) {
    	EntityPropertyManager.instance().writeToNBT(event.getSource(), tag);
    }
    
    public static void eventReadFromNBT(EventInfo<Entity> event, NBTTagCompound tag) {
    	EntityPropertyManager.instance().readFromNBT(event.getSource(), tag);
    }
    
    public static void eventClonePlayer(EventInfo<EntityPlayer> event, EntityPlayer old, boolean respawnedFromEnd) {
    	EntityPropertyManager.instance().copyToEntity(old, event.getSource());
    	if (!old.getUniqueID().equals(event.getSource().getUniqueID())) {
    		EntityPropertyManager.instance().entityDestroyed(old);
    	}
    }
    
    public static void eventCopyDataFromOld(EventInfo<Entity> event, Entity old) {
    	EntityPropertyManager.instance().copyToEntity(old, event.getSource());
    }
    
    public static void eventAddEntityCrashInfo(EventInfo<Entity> event, CrashReportCategory section) {
    	EntityPropertyManager.instance().addEntityCrashInfo(event.getSource(), section);
    }
    
    public static void eventTrackEntity(EventInfo<EntityTracker> event, Entity entity) {
    	if (EntityTrackerRegistry.instance().addEntityToTracker(event.getSource(), entity)) {
    		event.cancel();
    	} else {
    		EventHandler.eventTrackEntity(event.getSource(), entity);
    	}
    }
    
    public static void eventFunc_151260_c(ReturnEventInfo<EntityTrackerEntry, Packet> event) {
    	Packet result = EntityTrackerRegistry.instance().getSpawnPacket(event.getSource());
    	if (result != null) {
    		event.setReturnValue(result);
    	} else {
    		EventHandler.eventFunc_151260_c(event);
    	}
    }
    
    public static void eventDoesBlockHaveSolidTopSurface(ReturnEventInfo<World, Boolean> event, IBlockAccess world, BlockPos pos) {
    	Block block = world.getBlockState(pos).getBlock();
    	if (block instanceof ISided) {
    		event.setReturnValue(((ISided)block).isSideSolid(world, pos, EnumFacing.UP));
    	}
    }
    
    public static void eventGetItemBurnTime(ReturnEventInfo<TileEntityFurnace, Integer> event, ItemStack stack) {
    	int result = FurnaceFuels.getItemBurnTime(stack);
    	if (result > 0) event.setReturnValue(result);
    }
    /*
    //=============Torch events======
    
    public static void eventOnNeighborChangeInternal(ReturnEventInfo<BlockTorch, Boolean> event, World world, BlockPos pos, IBlockState state) {
        EnumFacing opposite = ((EnumFacing)state.getValue(BlockTorch.FACING)).getOpposite();
        pos = pos.offset(opposite);
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof ISided) {
        	BlockTorch torch = event.getSource();
        	if (((ISided)state.getBlock()).isSideSolid(world, pos, opposite)) {
        		event.setReturnValue(true);
        	}
        }
    }
    
    public static void canPlaceAt(ReturnEventInfo<BlockTorch, Boolean> event, World world, BlockPos pos, EnumFacing facing) {
    	facing = facing.getOpposite();
    	pos = pos.offset(facing);
    	IBlockState state = world.getBlockState(pos);
    	if (state.getBlock() instanceof ISided) {
    		event.setReturnValue(((ISided)state.getBlock()).isSideSolid(world, pos, facing));
    	}
    }
    
    //==============================
     Possibly out of our scope*/
}
