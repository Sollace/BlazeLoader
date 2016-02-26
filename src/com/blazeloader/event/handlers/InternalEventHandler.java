package com.blazeloader.event.handlers;

import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.command.CommandHandler;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
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
import com.blazeloader.util.version.Versions;

/**
 * Event handler for events that are not passed to mods, but rather to BL itself
 */
public class InternalEventHandler {
    public static void eventCreateNewCommandManager(CallbackInfoReturnable<CommandHandler> info) {
        info.setReturnValue(BLMain.instance().getCommandHandler());
    }
    
	public static void eventGetModName(CallbackInfoReturnable<String> info) {
		info.setReturnValue(retrieveBrand(info.getReturnValue()));
	}

	public static String retrieveBrand(String inheritedBrand) {
		String brand = ApiGeneral.getBrand();
		if (inheritedBrand != null && !(inheritedBrand.isEmpty() || "vanilla".contentEquals(inheritedBrand) || "LiteLoader".contentEquals(inheritedBrand))) {
			return inheritedBrand + " / " + brand;
		}
		return brand;
	}

	public static void eventPopulateChunk(Chunk sender, IChunkProvider providerOne, IChunkProvider providerTwo, int chunkX, int chunkZ) {
		if (UnpopulatedChunksQ.instance().pop(sender)) {
			Random random = new Random(sender.getWorld().getSeed());
			long seedX = random.nextLong() >> 2 + 1l;
			long seedZ = random.nextLong() >> 2 + 1l;
			long chunkSeed = (seedX * sender.xPosition + seedZ * sender.zPosition) ^ sender.getWorld().getSeed();

			List<IChunkGenerator> generators = ApiWorld.getGenerators();
			for (IChunkGenerator i : generators) {
				random.setSeed(chunkSeed);
				try {
					i.populateChunk(sender, providerOne, providerTwo, chunkX, chunkZ, random);
				} catch (Throwable e) {
					throw new ReportedException(CrashReport.makeCrashReport(e, "Exception during mod chunk populating"));
				}
			}
		}
	}
	
	public static void eventOnEntityRemoved(Entity entity) {
		if (entity.isDead) {
			if (!(entity instanceof EntityPlayer)) {
				EntityPropertyManager.instance().entityDestroyed(entity);
			}
		}
	}
	
    public static void eventClonePlayer(EntityPlayer sender, EntityPlayer old, boolean respawnedFromEnd) {
    	EntityPropertyManager.instance().copyToEntity(old, sender);
    	if (!old.getUniqueID().equals(sender.getUniqueID())) {
    		EntityPropertyManager.instance().entityDestroyed(old);
    	}
    }
    
    public static void eventTrackEntity(EntityTracker sender, CallbackInfo info, Entity entity) {
    	if (EntityTrackerRegistry.instance().addEntityToTracker(sender, entity)) {
    		info.cancel();
    	} else if (Versions.isClient()) {
    		EventHandler.eventTrackEntity(sender, entity);
    	}
    }
    
    public static void eventGetSpawnPacket(EntityTrackerEntry sender, CallbackInfoReturnable<Packet> info) {
    	Packet result = EntityTrackerRegistry.instance().getSpawnPacket(sender);
    	if (result != null) {
    		info.setReturnValue(result);
    	} else {
    		EventHandler.eventGetSpawnPacket(sender, info);
    	}
    }
    
    public static void eventDoesBlockHaveSolidTopSurface(CallbackInfoReturnable<Boolean> info, IBlockAccess access, BlockPos pos) {
    	Block block = access.getBlockState(pos).getBlock();
    	if (block instanceof ISided) {
    		info.setReturnValue(((ISided)block).isSideSolid(access, pos, EnumFacing.UP));
    	}
    }
    
    public static void eventGetItemBurnTime(CallbackInfoReturnable<Integer> info, ItemStack stack) {
    	int result = FurnaceFuels.getItemBurnTime(stack);
    	if (result > 0) info.setReturnValue(result);
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
