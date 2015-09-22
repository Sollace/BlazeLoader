package com.blazeloader.api.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FurnaceFuels {
	
	public static int getItemBurnTime(ItemStack stack) {
		if (stack == null) return 0;
		Item item = stack.getItem();
		if (item instanceof IFuel) {
			return ((IFuel)item).getBurnTime(stack);
		}
		return 0;
	}
}
