package com.blazeloader.api.toolset;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipesTools;

import java.util.List;

import com.blazeloader.api.recipe.ApiCrafting;
import com.blazeloader.api.recipe.ICraftingManager;
import com.google.common.collect.Lists;

/**
 * Factory class for generating recipes for a full se of tools.
 */
public class ToolRecipes {
    private final List<String[]> patterns = Lists.newArrayList(new RecipesTools().recipePatterns);
    
    private final ICraftingManager manager;
	
    private Item stickMaterial = Items.stick;
    
    public ToolRecipes() {
        this(ApiCrafting.getVanillaCraftingManager());
    }

    public ToolRecipes(ICraftingManager CraftingManager) {
        manager = CraftingManager;
    }
    
    /**
     * Generates recipes for the given material and tool items.
     * @param toolMaterial	Material item to use
     * @param tools			Tools that must be crafted.
     */
    public void AddToolSetRecipes(Item toolMaterial, Item... tools) {
        for (int i = 0; i < tools.length && i < patterns.size(); i++) {
            manager.addRecipe(new ItemStack(tools[i]), patterns.get(i), '#', stickMaterial, 'X', toolMaterial);
        }
    }
    
    /**
     * Sets the material to use for the handles of tools.
     */
    public ToolRecipes setStick(Item stick) {
    	stickMaterial = stick;
    	return this;
    }
    
    public ToolRecipes addPattern(String[] pattern) {
    	patterns.add(pattern);
    	return this;
    }
}
