package com.blazeloader.api.recipe;

import net.minecraft.item.ItemStack;

/**
 * Interface for items that want to serve as a fuel for furnaces.
 */
public interface IFuel {
	
	/**
	 * Gets the number of ticks that this item can power a furnace.
	 * 
	 * @param stack		The itemstack
	 * @return	Number of ticks to burn or zero if not a valid fuel
	 */
	public int getBurnTime(ItemStack stack);
}
