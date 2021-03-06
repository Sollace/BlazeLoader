package com.blazeloader.api.entity.profession;

import java.util.Random;

import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * A custom villager profession
 */
public interface IProfession {
	
	/**
	 * Checks if the given stack is sufficient for the villager to begin mating.
	 * <p>
	 * Called on each item in the villager's inventory.
	 * 
	 * @param stack				Stack to check
	 * @param stackMultiplier	Multiplier for the stack size
	 * 
	 * @return	True if it may mate, false otherwise
	 */
	public boolean checkMatingConditions(ItemStack stack, int stackMultiplier);
	
	public int getStackMultiplier();
	
	public int getNewCareer(Random rand);
	
	/**
	 * Gets the entries for a villager's trades.
	 * 
	 * @param villager	The villager
	 */
	public ITradeList[] getTradeList(int careerId, int careerLevel);
	
	/**
	 * Gets a display name for the villager.
	 */
	public ITextComponent getDisplayName();
	
	/**
	 * Gets a villager texture location for this profession.
	 * 
	 * @param villager	The villager
	 */
	public ResourceLocation getResourceLocation();
	
	/**
	 * Sets profession specific AI tasks for the given villager.
	 * 
	 * @param aiTasks the villager's set of AI tasks.
	 */
	public void setAdditionalAItasks(EntityAITasks aiTasks);
}
