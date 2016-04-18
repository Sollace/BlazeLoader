package com.blazeloader.util.data;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTWritable {
	
	/**
	 * @param tagCompound
	 */
	public void writeToNBT(NBTTagCompound tagCompound);
	
	/**
	 * 
	 * @param tagCompound
	 */
	public void readFromNBT(NBTTagCompound tagCompound);
}
