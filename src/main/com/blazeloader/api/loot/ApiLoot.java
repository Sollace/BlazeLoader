package com.blazeloader.api.loot;

import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

public class ApiLoot {
	/**
     * Adds a custom loot table to the game.
     * If one with the same name already exists will delegate to an alternate and return that one instead.
     * <br>
     * It is up to modders to make use of this table in their own code.
     * 
     * @return ResourceLocation for the registered table.
     */
    public static ResourceLocation registerLootTable(ResourceLocation id) {
    	String originalPath = id.getResourcePath();
    	Set<ResourceLocation> registered = getAllLootTables();
    	int i = 1;
    	while (registered.contains(id)) {
    		id = new ResourceLocation(id.getResourceDomain(), originalPath + "_" + i++);
    	}
    	return LootTableList.register(id);
    }
    
    /**
     * Gets an unmodifiable set of all currently registered loot table ids.
     */
    public static Set<ResourceLocation> getAllLootTables() {
    	return LootTableList.getAll();
    }
}
