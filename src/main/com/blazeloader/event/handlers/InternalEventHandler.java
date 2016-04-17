package com.blazeloader.event.handlers;

import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandHandler;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.blazeloader.api.ApiGeneral;
import com.blazeloader.api.block.ISided;
import com.blazeloader.api.block.fluid.Fluid;
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
    public static void eventCreateNewCommandManager(MinecraftServer sender, CallbackInfoReturnable<CommandHandler> info) {
        info.setReturnValue(BLMain.instance().getCommandHandler(sender));
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
    	info.setReturnValue(((ISided)access.getBlockState(pos).getBlock()).isSideSolid(access, pos, EnumFacing.UP));
    }
    
    public static void eventCanBlockFreeze(World sender, BlockPos pos, boolean noWaterAdj, CallbackInfoReturnable<Boolean> info) {
    	if (sender.isValid(pos.add(-pos.getX(), 0, -pos.getZ()))) {
    		BiomeGenBase biomegenbase = sender.getBiomeGenForCoords(pos);
            if (biomegenbase.getFloatTemperature(pos) < 0.15f) {
            	IBlockState state = sender.getBlockState(pos);
            	Block block = state.getBlock();
            	if (block instanceof Fluid) {
            		Fluid fluid = (Fluid)block;
            		if (fluid.canFreeze(sender, pos, state)) {
	            		if (!noWaterAdj) {
	            			info.setReturnValue(true);
	            			return;
	            		}
	            		for (EnumFacing i : EnumFacing.Plane.HORIZONTAL.facings()) {
	            			if (!fluid.isAdjacentWater(sender, pos, i, state)) {
	            				info.setReturnValue(true);
	            				return;
	            			}
	            		}
            		}
            		info.setReturnValue(false);
            	}
            }
    	}
    }
    
    public static void eventGetFlowDirection(IBlockAccess w, BlockPos pos, CallbackInfoReturnable<Double> info) {
    	Block block = w.getBlockState(pos).getBlock();
    	if (!(block instanceof Fluid)) return;
    	Vec3d vec3 = ((Fluid)block).getFlowingBlock().getFlowVector(w, pos);
        info.setReturnValue(vec3.xCoord == 0 && vec3.zCoord == 0 ? -1000 : MathHelper.atan2(vec3.zCoord, vec3.xCoord) - (Math.PI / 2));
    }
    
    public static IBlockState getFluidFrozenState(World sender, BlockPos pos, IBlockState state) {
    	if (state == Blocks.ice.getDefaultState()) {
			Block block = sender.getBlockState(pos).getBlock();
			if (block instanceof Fluid) {
				return ((Fluid)block).getFrozenState();
			}
		}
		return state;
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
