package com.blazeloader.api.item;

import java.lang.reflect.Field;

import com.google.common.collect.Lists;
import com.mumfrey.liteloader.client.ducks.IMutableRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

/**
 * Api functions for items.
 */
public class ApiItem {
	private static int currFreeItemId = 1;
	
    /**
     * Utility method to register an item.
     * Will assign a name to the item before registering.
     * <p>
     * Names will have the following format:
     * <br>
     * {mod}.{item name}
     * <p>
     * Registers an item in the game registry.
     *
     * @param id    The item ID
     * @param name  The item name
     * @param item  The item itself
     *
     * @return the item for simplicity
     */
    public static <T extends Item> T quickRegisterItem(int id, String mod, String name, T item) {
        return registerItem(id, new ResourceLocation(mod, name), (T)item.setUnlocalizedName(mod + "." + name));
    }
	
    /**
     * Registers an item in the game registry.
     *
     * @param id    The item ID
     * @param name  The item name
     * @param item  The item itself
     *
     * @return the item for simplicity
     */
    public static <T extends Item> T registerItem(int id, String mod, String name, T item) {
        return registerItem(id, new ResourceLocation(mod, name), item);
    }

    /**
     * Registers an item in the game registry.
     *
     * @param id    The item ID
     * @param name  The name to register the item as
     * @param item  The item itself
     *
     * @return the item for simplicity
     */
    public static <T extends Item> T registerItem(int id, ResourceLocation name, T item) {
    	boolean exists = Item.itemRegistry.containsKey(name);
    	if (exists) {
    		Item existing = Item.itemRegistry.getObject(name);
    		((IMutableRegistry<ResourceLocation, Item>)Item.itemRegistry).removeObjectFromRegistry(name);
    		try {
	    		for (Field field : Items.class.getDeclaredFields()) {
	                if (field.get(null).equals(existing)) {
	                    field.set(null, item);
	                }
	            }
    		} catch (Exception e) {}
    	}
    	Item.itemRegistry.register(id, name, item);
        return item;
    }
    
    /**
     * Registers an ItemBlock in the game registry for the given block.
     *
     * @param block  The block itself
     *
     * @return the item for simplicity
     */
    public static ItemBlock registerItemBlock(Block block) {
        return registerItemBlock(block, new ItemBlock(block));
    }
    
    /**
     * Registers an ItemBlock in the game registry for the given block.
     *
     * @param block  The item itself
     * @param item     An ItemBlock to register with the given block
     *
     * @return the item for simplicity
     */
    public static <T extends ItemBlock> T registerItemBlock(Block block, T item) {
        return registerItemBlock(Block.getIdFromBlock(block), (ResourceLocation) Block.blockRegistry.getNameForObject(block), block, item);
    }
    
    /**
     * Registers an ItemBlock in the game registry for the given block.
     *
     * @param id    The item ID
     * @param name  The name to register the item as
     * @param item  The item itself
     *
     * @return the item for simplicity
     */
    public static <T extends ItemBlock> T registerItemBlock(int id, ResourceLocation name, Block block, T item) {
        registerItem(id, name, item);
        Item.BLOCK_TO_ITEM.put(block, item);
        return item;
    }
    
    /**
     * Registers names for all the variants the given item has.
     * 
     * @param item		The item
     * @param variants	Names for all the item's variants
     */
    public static void registerItemVariantNames(Item item, String... variants) {
    	ItemRegistry.instance().registerVariantNames(item, Lists.newArrayList(variants));
    }
    
    /**
     * Checks if an item has been registered for the given id.
     * 
     * @param id	Id to check
     * 
     * @return true if it is free, false otherwise
     */
    public static boolean isIdFree(int id) {
    	return getItemById(id) == null;
    }
    
    /**
     * Gets a free item ID.
     *
     * @return return a free item ID.
     */
    public static int getFreeItemId() {
        while (!isIdFree(currFreeItemId)) {
            currFreeItemId++;
        }
        return currFreeItemId++;
    }
    
    /**
     * Gets the item by it's associated block.
     * 
     * @param block	The block
     * 
     * @return An item for the block
     */
    public static Item getItemByBlock(Block block) {
    	return Item.getItemFromBlock(block);
    }
    
    /**
     * Gets an item from by given id
     * 
     * @param id The id
     * 
     * @return and item associated with that id or null
     */
    public static Item getItemById(int id) {
    	return Item.getItemById(id);
    }
    
    /**
     * Gets the id associated with the given Item.
     * 
     * @param item The item
     * 
     * @return the item's id
     */
    public static int getItemId(Item item) {
    	return Item.getIdFromItem(item);
    }
    
    /**
     * Gets the name of an item.
     *
     * @param item The Item to get the name for
     * @return Return a string of the name belonging to param item
     */
    public static ResourceLocation getItemName(Item item) {
        return (ResourceLocation)Item.itemRegistry.getNameForObject(item);
    }
    
    /**
     * Gets the name of an item.
     *
     * @param item The Item to get the name for
     * @return Return a string of the name belonging to param item
     */
    public static String getStringItemName(Item item) {
        return getItemName(item).toString();
    }
}
