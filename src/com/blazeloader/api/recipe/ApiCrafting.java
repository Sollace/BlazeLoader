package com.blazeloader.api.recipe;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.*;

public class ApiCrafting {

	private static final Map<Integer, ICraftingManager> instances = new HashMap<Integer, ICraftingManager>();
	private static int nextId = 1;
	
	static {
		instances.put(0, new BLCraftingManager(0, CraftingManager.getInstance().getRecipeList()));
	}
	
	/**
	 * Gets a wrapped instance of the normal CraftingManager.
	 * @return Manager instance of CraftingManager
	 */
	public static ICraftingManager getVanillaCraftingManager() {
		return instances.get(0);
	}
	
	/**
	 * Intended for compatibility with mods that implemement their
	 * own CraftingManagers based off of the vanilla one.
	 * 
	 * Will parse a vanilla Minecraft CraftingManager to a Blazeloader apis compatible Manager.
	 * 
	 * It is not recommended to use this method often. Rather start off with a Manager
	 * or keep a reference to the converted result for later use.
	 * 
	 * @param manager	CraftingManager to convert
	 * 
	 * @return Manager corresponding to the given CraftingManager
	 */
	public static ICraftingManager toManager(CraftingManager manager) {
		for (ICraftingManager i : instances.values()) {
			if (i.equals(manager)) return i;
		}
		return createCraftingManager((ArrayList<IRecipe>)manager.getRecipeList());
	}
	
	/**
	 * Gets a CraftingManager from the pool by it's unique id.
	 * 
	 * @param id	integer id of the manager you'd like to find.
	 * 
	 * @return Manager or null if not found.
	 */
	public static ICraftingManager getManagerFromId(int id) {
		return instances.get(id);
	}
	
	/**
	 * Creates a brand spanking **new** Crafting Manager.
	 */
	public static ICraftingManager createCraftingManager() {
		return createCraftingManager(new ArrayList<IRecipe>());
	}

	private static ICraftingManager createCraftingManager(ArrayList<IRecipe> startingRecipes) {
		int id = nextId++;
		instances.put(id, new BLCraftingManager(id, startingRecipes));
		return instances.get(id);
	}
	
	public static final class BLCraftingManager implements ICraftingManager {
		private final int id;
		private final List<IRecipe> recipes;

		private BLCraftingManager(int n, List<IRecipe> recipes) {
			id = n;
			this.recipes = recipes;
		}
		
		public int getId() {
			return id;
		}
		
		public List<IRecipe> getRecipeList() {
			return Collections.unmodifiableList(recipes);
		}
		
	    public ShapedRecipe addRecipe(ItemStack output, Object... input) {
	    	ShapedRecipe result = createShaped(output, false, input);
	        recipes.add(result);
	        return result;
	    }
	    
	    public ShapelessRecipe addShapelessRecipe(ItemStack output, Object... input) {
	    	ShapelessRecipe result = createShapeless(output, false, input);
	        recipes.add(result);
	        return result;
	    }
	    
	    public ReversibleShapedRecipe addReverseRecipe(ItemStack output, Object... input) {
	    	ShapedRecipe result = createShaped(output, true, input);
	        recipes.add(result);
	        return (ReversibleShapedRecipe)result;
	    }
	    
	    public ReversibleShapelessRecipe addReverseShapelessRecipe(ItemStack output, Object... input) {
	    	ShapelessRecipe result = createShapeless(output, true, input);
	        recipes.add(result);
	        return (ReversibleShapelessRecipe)result;
	    }
	    
	    public void addRecipe(IRecipe recipe) {
	        recipes.add(recipe);
	    }
	    
	    public boolean removeRecipe(IRecipe recipe) {
	    	return recipes.remove(recipe);
	    }
	    
	    public int removeRecipe(ItemStack result) {
	    	return removeRecipe(result, -1);
	    }
	    
	    public int removeRecipe(ItemStack result, int maxRemovals) {
	    	int count = 0;
	    	for (int i = 0; i < recipes.size(); i++) {
	    		if (recipes.get(i).getRecipeOutput() == result) {
	    			count++;
	    			recipes.remove(i);
	    			if (maxRemovals > 0 && count >= maxRemovals) return count;
	    		}
	    	}
	    	return count;
	    }
	    
	    private ShapedRecipe createShaped(ItemStack output, boolean reverse, Object... input) {
	        String recipe = "";
	        int index = 0;
	        int width = 0;
	        int height = 0;

	        if (input[index] instanceof String[]) {
	            for (String i : (String[])input[index++]) {
	                ++height;
	                width = i.length();
	                recipe += i;
	            }
	        } else {
	            while (input[index] instanceof String) {
	                String line = (String)input[index++];
	                ++height;
	                width = line.length();
	                recipe += line;
	            }
	        }

	        HashMap<Character, ItemStack> stackmap = new HashMap<Character, ItemStack>();
	        while (index < input.length) {
				char var13 = (Character) input[index];
				ItemStack var15 = null;
				if (input[index + 1] instanceof Item) {
	                var15 = new ItemStack((Item)input[index + 1]);
	            } else if (input[index + 1] instanceof Block) {
	                var15 = new ItemStack((Block)input[index + 1], 1, 32767);
	            } else if (input[index + 1] instanceof ItemStack) {
	                var15 = (ItemStack)input[index + 1];
	            }
	            stackmap.put(var13, var15);
	            index += 2;
	        }

	        ItemStack[] stacks = new ItemStack[width * height];
	        for (int i = 0; i < width * height; i++) {
	            char key = recipe.charAt(i);
	            if (stackmap.containsKey(key)) {
	                stacks[i] = stackmap.get(key).copy();
	            } else {
	                stacks[i] = null;
	            }
	        }
	        if (reverse) return new ReversibleShapedRecipe(width, height, stacks, output);
	        return new ShapedRecipe(width, height, stacks, output);
	    }
	    
	    private ShapelessRecipe createShapeless(ItemStack output, boolean reverse, Object ... input) {
	        ArrayList itemStacks = Lists.newArrayList();
			for (Object obj : input) {
				if (obj instanceof ItemStack) {
					itemStacks.add(((ItemStack) obj).copy());
				} else if (obj instanceof Item) {
					itemStacks.add(new ItemStack((Item) obj));
				} else {
					if (!(obj instanceof Block))
						throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + obj.getClass().getName() + "!");
					itemStacks.add(new ItemStack((Block) obj));
				}
			}
			if (reverse) return new ReversibleShapelessRecipe(output, itemStacks);
	        return new ShapelessRecipe(output, itemStacks);
	    }
	    
	    public ItemStack findMatchingRecipe(InventoryCrafting inventory, World world) {
	        for (IRecipe i : recipes) {
	        	if (i.matches(inventory, world)) return i.getCraftingResult(inventory);
	        }
	        return null;
	    }
	    
	    public ItemStack[] findRecipeInput(ItemStack recipeOutput, int width, int height) {
	    	for (IRecipe i : recipes) {
	    		if (i instanceof IReversibleRecipe) {
	    			IReversibleRecipe recipe = ((IReversibleRecipe)i);
	    			if (recipe.matchReverse(recipeOutput, width, height)) return recipe.getRecipeInput();
	    		}
	    	}
	    	return null;
	    }
	    
	    public ItemStack[] getUnmatchedInventory(InventoryCrafting inventory, World world) {
	        for (IRecipe i : recipes) {
	            if (i.matches(inventory, world)) return i.getRemainingItems(inventory);
	        }
	        ItemStack[] newInventory = new ItemStack[inventory.getSizeInventory()];
	        for (int i = 0; i < newInventory.length; i++) {
	            newInventory[i] = inventory.getStackInSlot(i);
	        }
	        return newInventory;
	    }
	    
	    public boolean equals(Object obj) {
			if (obj instanceof ICraftingManager) {
				return ((ICraftingManager) obj).getId() == id;
			}
	    	if (obj instanceof CraftingManager) {
	    		return recipes.equals(((CraftingManager)obj).getRecipeList());
	    	}
	    	if (obj instanceof List<?>) {
	    		return obj.equals(recipes);
	    	}
	    	return super.equals(obj);
	    }
	}
}