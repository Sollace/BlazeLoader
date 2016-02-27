package com.blazeloader.api.world;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Collection;

import com.blazeloader.api.block.IRotateable;
import com.blazeloader.bl.interop.ForgeWorldAccess;
import com.blazeloader.util.reflect.Func;
import com.blazeloader.util.reflect.Reflect;
import com.blazeloader.util.reflect.Var;
import com.blazeloader.util.version.Versions;
import com.google.common.collect.ImmutableSet;

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
	
    private static Func<Block, IRotateable, EnumFacing[]> _getValidRotations;
    
	protected static boolean isSideSolid(World worldObj, BlockPos pos, EnumFacing side, boolean def) {
		if (Versions.isForgeInstalled()) {
			return ((ForgeWorldAccess)worldObj).isSideSolid(pos, side, def);
		}
		return def;
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
		public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean def) {
			return ForgeWorld.isSideSolid(worldObj, pos, side, def);
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
