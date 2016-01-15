package com.blazeloader.api.world;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;

import java.util.Collection;

import com.blazeloader.api.block.IRotateable;
import com.blazeloader.bl.interop.ForgeWorldAccess;
import com.blazeloader.util.reflect.Func;
import com.blazeloader.util.reflect.Reflect;
import com.blazeloader.util.reflect.Var;
import com.blazeloader.util.version.Versions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

/**
 * Reflection based access to world methods added by forge.
 */
public final class ForgeWorld {
	
    /**
     * Gets an accessor to Forge methods on the given Minecraft world.
     * This may be used in cases where a mod specifically requires access to forge's methods.
     * 
     * @param w		A world instance to apply to
     */
    public static ForgeWorldAccess getForgeWorld(World w) {
    	return new ForgeWorldObj(w);
    }
    
	private static final Var<World, Double> MAX_ENTITY_RADIUS = Reflect.lookupField(World.class, double.class, "MAX_ENTITY_RADIUS");
	
	private static Func<World, ForgeWorldAccess, Boolean> _isSideSolid;
    private static Func<World, ForgeWorldAccess, ImmutableSetMultimap> _getPersistentChunks;
    private static Func<World, ForgeWorldAccess, Integer> _countEntities;
    private static Func<World, ForgeWorldAccess, MapStorage> _getPerWorldStorage;
    private static Func<World, ForgeWorldAccess, Integer> _getBlockLightOpacity;
	
    private static Func<Block, IRotateable, Boolean> _rotateBlock;
    private static Func<Block, IRotateable, EnumFacing[]> _getValidRotations;
    
	protected static boolean isSideSolid(World worldObj, BlockPos pos, EnumFacing side, boolean def) {
		if (Versions.isForgeInstalled()) {
			//return worldObj.isSideSolid(pos, side, def);
	    	if (_isSideSolid == null) {
	    		_isSideSolid = Reflect.lookupMethod(ForgeWorldAccess.class, World.class, boolean.class, "isSideSolid", BlockPos.class, EnumFacing.class, boolean.class);
	    	}
	    	if (_isSideSolid.valid()) {
	    		try {
	    			return _isSideSolid.getLambda(worldObj).isSideSolid(pos, side, def);
	    		} catch (Throwable e) {
	    			_isSideSolid.invalidate();
	    		}
	    	}
		}
		return def;
	}
	
	protected static <Ticket> ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunks(World worldObj) {
		if (Versions.isForgeInstalled()) {
			//return worldObj.getPersistentChunks();
	    	if (_getPersistentChunks == null) {
	    		_getPersistentChunks = Reflect.lookupMethod(ForgeWorldAccess.class, World.class, ImmutableSetMultimap.class, "getPersistentChunks");
	    	}
	    	if (_getPersistentChunks.valid()) {
		    	ImmutableSetMultimap<ChunkCoordIntPair, Ticket> result;
		    	try {
		    		result = _getPersistentChunks.getLambda(worldObj).getPersistentChunks();
		    	} catch (Throwable e) {
		    		_getPersistentChunks.invalidate();
		    		result = null;
		    	}
		    	return result == null ? ImmutableSetMultimap.<ChunkCoordIntPair, Ticket>of() : result;
	    	}
		}
		return ImmutableSetMultimap.<ChunkCoordIntPair, Ticket>of();
	}
	
	protected static int getBlockLightOpacity(World worldObj, BlockPos pos) {
		if (Versions.isForgeInstalled()) {
			//return worldObj.getBlockLightOpacity(pos);
			if (_getBlockLightOpacity == null) {
				_getBlockLightOpacity = Reflect.lookupMethod(ForgeWorldAccess.class, World.class, int.class, "getBlockLightOpacity", BlockPos.class);
			}
			if (_getBlockLightOpacity.valid()) {
				try {
					return _getBlockLightOpacity.getLambda(worldObj).getBlockLightOpacity(pos);
				} catch (Throwable e) {
					_getBlockLightOpacity.invalidate();
		    	}
			}
		}
    	if (!worldObj.isValid(pos)) return 0;
        return worldObj.getChunkFromBlockCoords(pos).getBlockLightOpacity(pos);
	}
	
	protected static int countEntities(World worldObj, EnumCreatureType type, boolean forSpawnCount) {
		if (Versions.isForgeInstalled()) {
			//return worldObj.countEntities(type, forSpawnCount);
	    	if (_countEntities == null) {
	    		_countEntities = Reflect.lookupMethod(ForgeWorldAccess.class, World.class, int.class, "countEntities", EnumCreatureType.class, boolean.class);
	    	}
	    	if (_countEntities.valid()) {
		    	try {
					return _countEntities.getLambda(worldObj).countEntities(type, forSpawnCount);
				} catch (Throwable e) {
					_countEntities.invalidate();
				}
	    	}
		}
		return worldObj.countEntities(type.getCreatureClass());
	}
	
	protected static MapStorage getPerWorldStorage(World worldObj) {
		if (Versions.isForgeInstalled()) {
			//return worldObj.getPerWorldStorage();
			if (_getPerWorldStorage == null) {
				_getPerWorldStorage = Reflect.lookupMethod(ForgeWorldAccess.class, World.class, MapStorage.class, "getPerWorldStorage");
			}
			if (_getPerWorldStorage.valid()) {
				try {
					return _getPerWorldStorage.getLambda(worldObj).getPerWorldStorage();
				} catch (Throwable e) {
					_getPerWorldStorage.invalidate();
				}
			}
		}
		return worldObj.getMapStorage();
	}
	
	@Deprecated
	protected static boolean rotateBlock(Block block, World worldObj, BlockPos pos, EnumFacing axis) {
		if (Versions.isForgeInstalled()) {
			//return block.rotateBlock(worldObj, pos, axis);
			if (_rotateBlock == null) {
				_rotateBlock = Reflect.lookupMethod(IRotateable.class, Block.class, boolean.class, "rotateBlock", World.class, BlockPos.class, EnumFacing.class);
			}
			if (_rotateBlock.valid()) {
				try {
					return _rotateBlock.getLambda(block).rotateBlock(worldObj, pos, axis);
				} catch (Throwable e) {
					_rotateBlock.invalidate();
				}
			}
		}
		IBlockState state = worldObj.getBlockState(pos);
		for (IProperty i : (ImmutableSet<IProperty>)state.getProperties().keySet()) {
			if (i.getName().contentEquals("facing")) {
				worldObj.setBlockState(pos, state.cycleProperty(i));
				return true;
			}
		}
		return false;
	}
	
	protected static EnumFacing[] getValidRotations(Block block, World worldObj, BlockPos pos) {
		if (Versions.isForgeInstalled()) {
			//return block.getValidRotations(worldObj, pos);
			if (_getValidRotations == null) {
				_getValidRotations = Reflect.lookupMethod(IRotateable.class, Block.class, EnumFacing[].class, "getValidRotations", World.class, BlockPos.class);
			}
			if (_getValidRotations.valid()) {
				try {
					return _getValidRotations.getLambda(block).getValidRotations(worldObj, pos);
				} catch (Throwable e) {
					_getValidRotations.invalidate();
				}
			}
		}
		IBlockState state = worldObj.getBlockState(pos);
		for (IProperty i : (ImmutableSet<IProperty>)state.getProperties().keySet()) {
			if (i.getName().contentEquals("facing") && i.getValueClass() == EnumFacing.class) {
				Collection<EnumFacing> result = i.getAllowedValues();
				return result.toArray(new EnumFacing[result.size()]);
			}
		}
		return null;
	}
	
	protected static double getMaxEntitySize(World worldObj, double def) {
		//return worldObj.MAX_ENTITY_RADIUS;
		return MAX_ENTITY_RADIUS.get(worldObj, def);
	}
	
	protected static void setMaxEntitySize(World worldObj, double size) {
		//worldObj.MAX_ENTITY_RADIUS = size;
		MAX_ENTITY_RADIUS.set(worldObj, size);
	}
	
    protected static final class ForgeWorldObj implements ForgeWorldAccess {
		private final World worldObj;
		
		private ForgeWorldObj(World w) {
			worldObj = w;
		}

		@Override
		public boolean isSideSolid(BlockPos pos, EnumFacing side) {
			return isSideSolid(pos, side, false);
		}

		@Override
		public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean def) {
			return ForgeWorld.isSideSolid(worldObj, pos, side, def);
		}

		@Override
		public <Ticket> ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunks() {
			return ForgeWorld.getPersistentChunks(worldObj);
		}

		@Override
		public int getBlockLightOpacity(BlockPos pos) {
			return ForgeWorld.getBlockLightOpacity(worldObj, pos);
		}

		@Override
		public int countEntities(EnumCreatureType type, boolean forSpawnCount) {
			return ForgeWorld.countEntities(worldObj, type, forSpawnCount);
		}
		
		@Deprecated @Override
		public boolean rotateBlock(BlockPos pos, EnumFacing axis) {
			return ForgeWorld.rotateBlock(worldObj.getBlockState(pos).getBlock(), worldObj, pos, axis);
		}
		
		@Override
		public MapStorage getPerWorldStorage() {
			return ForgeWorld.getPerWorldStorage(worldObj);
		}

		@Override
		public double getMaxEntitySize(double def) {
			return ForgeWorld.getMaxEntitySize(worldObj, def);
		}

		@Override
		public void setMaxEntitySize(double size) {
			ForgeWorld.setMaxEntitySize(worldObj, size);
		}
    }
}
