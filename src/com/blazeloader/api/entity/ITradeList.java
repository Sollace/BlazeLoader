package com.blazeloader.api.entity;

import java.util.Random;

import net.minecraft.village.MerchantRecipeList;

public interface ITradeList {
	void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random);
}
