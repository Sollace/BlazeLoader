package com.blazeloader.api.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IPickListener {
	
	/**
	 * Called when he player presses the pick-block button whilst looking a this entity.
	 * Returns the item that must be picked up.
	 */
	public ItemStack onPlayerMiddleClick(EntityPlayer player);
}
