package com.blazeloader.api.recipe;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 * Custom implementation of the CraftingManager.
 * Supports additional functionality such as reverse crafting,
 * crafting areas greater than 3x3 and methods for removing recipes.
 */
public interface ICraftingManager extends Comparable<ICraftingManager> {
	
	/**
	 * Gets the unique integer id for this CraftingManager.
	 * Can be used to retrieve this manager again from the pool of CraftingManagers.
	 * 
	 * @return integer id
	 */
	public int getId();
	
	/**
	 * Returns an unmodifieable list of recipes registered to this CraftingManager.
	 * 
	 * @return List of Recipes
	 */
	public List<IRecipe> getRecipeList();
	
    /**
     * Adds a shaped recipe to this CraftingManager.
     * 
     * @param output	ItemStack output for this recipe
     * @param input		Strings of recipe pattern followed by chars mapped to Items/Blocks/ItemStacks
     */
	public ShapedRecipe addRecipe(ItemStack output, Object... input);
	
    /**
     * Adds a shapeless crafting recipe to this CraftingManager.
     *  
     * @param output	ItemStack output for this recipe
     * @param input		An array of ItemStack's Item's and Block's that make up the recipe.
     */
	public ShapelessRecipe addShapelessRecipe(ItemStack output, Object... input);
	
    /**
     * Adds a shaped recipe to this CraftingManager.
     * 
     * @param output	ItemStack output for this recipe
     * @param input		Strings of recipe pattern followed by chars mapped to Items/Blocks/ItemStacks
     */
	public ReversibleShapedRecipe addReverseRecipe(ItemStack output, Object... input);
	
    /**
     * Adds a shapeless crafting recipe to this CraftingManager.
     *  
     * @param output	ItemStack output for this recipe
     * @param input		An array of ItemStack's Item's and Block's that make up the recipe.
     */
	public ReversibleShapelessRecipe addReverseShapelessRecipe(ItemStack output, Object... input);
	
    /**
     * Adds an IRecipe to this RecipeManager.
     *  
     * @param recipe A recipe that will be added to the recipe list.
     */
	public void addRecipe(IRecipe recipe);
	
    /**
     * Removes the given recipe
     * 
     * @param recipe	recipe to be removed
     * 
     * @return true if the recipe was removed, false otherwise
     */
	public boolean removeRecipe(IRecipe recipe);
	
    /**
     * Removes recipes for the given item
     * 
     * @param result	ItemStack result of the recipe to be removed
     * 
     * @return total number of successful removals
     */
	public int removeRecipe(ItemStack result);
	
    /**
     * Removes recipes for the given item
     * 
     * @param result		ItemStack result of the recipe to be removed
     * @param maxRemovals	Maximum number of removals
     * 
     * @return total number of successful removals
     */
	public int removeRecipe(ItemStack result, int maxRemovals);
	
    /**
     * Retrieves the result of a matched recipe in this RecipeManager.
     * 
     * @param inventory		inventory containing the crafting materials
     * @param world			the world that the crafting is being done in (usually the world of the player)
     * 
     * @return ItemStack result or null if none match
     */
	public ItemStack findMatchingRecipe(InventoryCrafting inventory, World world);
	
	/**
     * Retrieves the input required to craft the given item.
     * 
     * @param recipeOutput	ItemStack you wish to uncraft
     * @param width			width of crafting table
     * @param height		height of crafting table
     * 
     * @return ItemStack[] array of inventory contents needed
     */
	public ItemStack[] findRecipeInput(ItemStack recipeOutput, int width, int height);
	 
    /**
     * Gets the remaining contents for the inventory after performing a craft.
     * 
     * @param inventory		inventory containing the crafting materials
     * @param world			the world that the crafting is being done in (usually the world of the player)
     * 
     * @return ItemStack[] array of remaining items
     */
	public ItemStack[] getUnmatchedInventory(InventoryCrafting inventory, World world);

	public default int compareTo(ICraftingManager o) {
		return o.getId() - getId();
	}
}
