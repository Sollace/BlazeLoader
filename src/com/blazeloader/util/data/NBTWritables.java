package com.blazeloader.util.data;

import com.blazeloader.api.block.ApiBlock;
import com.blazeloader.util.config.IStringable;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandResultStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.FoodStats;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSavedData;

public final class NBTWritables {
	public static INBTWritable wrap(Object o) {
		if (o instanceof INBTWritable) return (INBTWritable)o;
		return new NBTWritable(o);
	}
	
	public static <T> T unwrap(INBTWritable writable) {
		if (writable instanceof NBTWritable<?>) {
			return ((NBTWritable<T>)writable).wrapped;
		}
		return (T)writable;
	}
	
	public static <T> boolean canWriteToNBT(T wrapped) {
		return wrapped instanceof INBTWritable || wrapped instanceof Enum || wrapped instanceof Entity || wrapped instanceof TileEntity || wrapped instanceof BlockPos || wrapped instanceof IBlockState || wrapped instanceof ItemStack || wrapped instanceof WorldSavedData || wrapped instanceof FoodStats || wrapped instanceof PlayerCapabilities || wrapped instanceof GameRules || wrapped instanceof GameProfile || wrapped instanceof MerchantRecipeList || wrapped instanceof MobSpawnerBaseLogic || wrapped instanceof CommandResultStats || wrapped instanceof BaseAttributeMap || wrapped instanceof Village || wrapped instanceof IStringable;
	}
	
	public static <T> T readObjectFromNBT(T wrapped, NBTTagCompound tagCompound) {
		if (wrapped instanceof INBTWritable) {
			((INBTWritable)wrapped).readFromNBT(tagCompound);
		} else if (wrapped instanceof Enum) {
			return (T)Enum.valueOf(((Enum)wrapped).getClass(), tagCompound.getString("EnumValue"));
		} else if (wrapped instanceof Entity) {
			((Entity)wrapped).readFromNBT(tagCompound);
		} else if (wrapped instanceof TileEntity) {
			((TileEntity)wrapped).readFromNBT(tagCompound);
		} else if (wrapped instanceof BlockPos) {
			return (T)new BlockPos(tagCompound.getInteger("X"), tagCompound.getInteger("Y"), tagCompound.getInteger("Z"));
		} else if (wrapped instanceof IBlockState) {
			return (T)Block.getBlockFromName(tagCompound.getString("blockName")).getStateFromMeta(tagCompound.getInteger("metadata"));
		} else if (wrapped instanceof ItemStack) {
			((ItemStack)wrapped).readFromNBT(tagCompound);
		} else if (wrapped instanceof WorldSavedData) {
			((WorldSavedData)wrapped).readFromNBT(tagCompound);
		} else if (wrapped instanceof FoodStats) {
			((FoodStats)wrapped).readNBT(tagCompound);
		} else if (wrapped instanceof PlayerCapabilities) {
			((PlayerCapabilities)wrapped).readCapabilitiesFromNBT(tagCompound);
		} else if (wrapped instanceof GameRules) {
			if (tagCompound.hasKey("GameRules")) {
				((GameRules)wrapped).readGameRulesFromNBT(tagCompound.getCompoundTag("GameRules"));
			}
		} else if (wrapped instanceof GameProfile) {
			return (T)NBTUtil.readGameProfileFromNBT(tagCompound);
		} else if (wrapped instanceof MerchantRecipeList) {
			if (tagCompound.hasKey("MerchanRecipeList")) {
				((MerchantRecipeList)wrapped).readRecipiesFromTags(tagCompound.getCompoundTag("MerchanRecipeList"));
			}
		} else if (wrapped instanceof MobSpawnerBaseLogic) {
			((MobSpawnerBaseLogic)wrapped).readFromNBT(tagCompound);
		} else if (wrapped instanceof CommandResultStats) {
			((CommandResultStats)wrapped).func_179668_a(tagCompound);
		} else if (wrapped instanceof BaseAttributeMap) {
			SharedMonsterAttributes.func_151475_a((BaseAttributeMap)wrapped, tagCompound.getTagList("Attributes", 10));
		} else if (wrapped instanceof Village) {
			((Village)wrapped).readVillageDataFromNBT(tagCompound);
		} else if (wrapped instanceof IStringable) {
			return (T)((IStringable)wrapped).fromString(tagCompound.getString(wrapped.getClass().getSimpleName()));
		}
		return wrapped;
	}
	
	public static <T> void writeObjectToNBT(T wrapped, NBTTagCompound tagCompound) {
		if (wrapped instanceof INBTWritable) {
			((INBTWritable)wrapped).writeToNBT(tagCompound);
		} else if (wrapped instanceof Enum) {
			tagCompound.setString("EnumValue", ((Enum)wrapped).name().toUpperCase());
		} else if (wrapped instanceof Entity) {
			((Entity)wrapped).writeToNBT(tagCompound);
		} else if (wrapped instanceof TileEntity) {
			((TileEntity)wrapped).writeToNBT(tagCompound);
		} else if (wrapped instanceof BlockPos) {
			tagCompound.setInteger("X", ((BlockPos)wrapped).getX());
			tagCompound.setInteger("Y", ((BlockPos)wrapped).getY());
			tagCompound.setInteger("Z", ((BlockPos)wrapped).getZ());
		} else if (wrapped instanceof IBlockState) {
			tagCompound.setString("blockName", ApiBlock.getStringBlockName(((IBlockState)wrapped).getBlock()));
			tagCompound.setInteger("metadata", ((IBlockState)wrapped).getBlock().getMetaFromState((IBlockState)wrapped));
		} else if (wrapped instanceof ItemStack) {
			((ItemStack)wrapped).writeToNBT(tagCompound);
		} else if (wrapped instanceof WorldSavedData) {
			((WorldSavedData)wrapped).writeToNBT(tagCompound);
		} else if (wrapped instanceof FoodStats) {
			((FoodStats)wrapped).writeNBT(tagCompound);
		} else if (wrapped instanceof PlayerCapabilities) {
			((PlayerCapabilities)wrapped).writeCapabilitiesToNBT(tagCompound);
		} else if (wrapped instanceof GameRules) {
			tagCompound.setTag("GameRules", ((GameRules)wrapped).writeGameRulesToNBT());
		} else if (wrapped instanceof GameProfile) {
			NBTUtil.writeGameProfile(tagCompound, (GameProfile)wrapped);
		} else if (wrapped instanceof MerchantRecipeList) {
			tagCompound.setTag("MerchanRecipeList", ((MerchantRecipeList)wrapped).getRecipiesAsTags());
		} else if (wrapped instanceof MobSpawnerBaseLogic) {
			((MobSpawnerBaseLogic)wrapped).writeToNBT(tagCompound);
		} else if (wrapped instanceof CommandResultStats) {
			((CommandResultStats)wrapped).func_179670_b(tagCompound);
		} else if (wrapped instanceof BaseAttributeMap) {
			SharedMonsterAttributes.writeBaseAttributeMapToNBT((BaseAttributeMap)wrapped);
		} else if (wrapped instanceof Village) {
			((Village)wrapped).writeVillageDataToNBT(tagCompound);
		} else if (wrapped instanceof IStringable) {
			tagCompound.setString(wrapped.getClass().getSimpleName(), wrapped.toString());
		}
	}
	
	private static final class NBTWritable<T> implements INBTWritable {
		
		private T wrapped;
		
		private NBTWritable(T wrapped) {
			this.wrapped = wrapped;
		}
		
		@Override
		public void writeToNBT(NBTTagCompound tagCompound) {
			writeObjectToNBT(wrapped, tagCompound);
		}
		
		@Override
		public void readFromNBT(NBTTagCompound tagCompound) {
			wrapped = readObjectFromNBT(wrapped, tagCompound);
		}
	}
}
