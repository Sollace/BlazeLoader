package com.blazeloader.api.entity.profession;

import java.util.Random;

import net.minecraft.village.MerchantRecipeList;

/**
 * Interface for a villager's trade list.
 */
public interface ITradeList {
	/**
	 * Adds this TradeList's recipes to the villager's inventory.
	 * 
	 * @param recipeList	List to add to
	 * @param random		A random generator specific to the parent villager
	 */
	void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random);
}
