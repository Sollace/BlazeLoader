package com.blazeloader.api.privileged;

import com.mumfrey.liteloader.transformers.access.Accessor;

import net.minecraft.village.MerchantRecipeList;

/**
 * Accessor methods for private fields on EntityVillager.
 */
@Accessor("net.minecraft.entity.passive.EntityVillager") public interface IEntityVillager {
	
	/**Returns true if this villager has performed its initial setup of AI Tasks and tradelists.*/
	@Accessor("areAdditionalTasksSet") public abstract boolean areAdditionalTasksSet();
	
	/**Gets the current level of trade achieved by this villager.*/
	@Accessor("careerLevel") public abstract int getCareerLevel();
	
	/**Sets this villager's trade level to the given value.
	 * @param level Trade value, must be positive.*/
	@Accessor("careerLevel") public abstract void setCareerLevel(int level);
	
	/**Gets the id for this villager's current career.*/
	@Accessor("careerId") public abstract int getCareer();
	
	/**Sets this villager's career to the given id.*/
	@Accessor("careerId") public abstract void setCareer(int career);
	
	/**Gets the collection of recipes this villager currently has to offer.*/
	@Accessor("buyingList") public abstract MerchantRecipeList getMerchentRecipes();
	
	/**Sets this villager's recipe list.*/
	@Accessor("buyingList") public abstract void setMerchentRecipes(MerchantRecipeList list);
}
