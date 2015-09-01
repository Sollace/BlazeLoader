package com.blazeloader.api.privileged;

import com.mumfrey.liteloader.transformers.access.Accessor;
import com.mumfrey.liteloader.transformers.access.ObfTableClass;

import net.minecraft.village.MerchantRecipeList;

/**
 * Accessor methods for private field on EntityVillager.
 * <p>
 * 
 */
@ObfTableClass(Privileged.EntityVillager.class) @Accessor("clazz") public interface IEntityVillager {
	@Accessor("areAdditionalTasksSet") public abstract boolean areAdditionalTasksSet();
	@Accessor("careerLevel") public abstract int getCareerLevel();
	@Accessor("careerLevel") public abstract void setCareerLevel(int level);
	@Accessor("careerId") public abstract int getCareer();
	@Accessor("careerId") public abstract void setCareer(int career);
	@Accessor("buyingList") public abstract MerchantRecipeList getMerchentRecipes();
	@Accessor("buyingList") public abstract void setMerchentRecipes(MerchantRecipeList list);
}
