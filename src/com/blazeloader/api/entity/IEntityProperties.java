package com.blazeloader.api.entity;

import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Interface for entity extension classes
 */
public interface IEntityProperties {
	
	/**
	 * Notifies this properties of who its owner is.
	 * <p>
	 * The entity a property instance is attached to can change under some circumstances.
	 */
	public void setOwningEntity(Entity owner);
	
	/**
	 * Occurs whilst the entity is being constructed.
	 */
	public void entityInit(Entity e, World w);
	
	/**
	 * Called to update the entity's position/logic.
	 */
	public void onEntityUpdate(Entity e);
	
	/**
	 * @param tagCompound
	 */
	public void writeToNBT(NBTTagCompound tagCompound);
	
	/**
	 * 
	 * @param tagCompound
	 */
	public void readFromNBT(NBTTagCompound tagCompund);
	
	/**
	 * Used if the game crashes. Put stuff you want to know in the crash report from here.
	 * 
	 * @param catagory	CrashReportCategory for this IEntityProperty.
	 */
	public void addEntityCrashInfo(CrashReportCategory catagory);
}
