package com.blazeloader.api.item;

import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRegistry {
	private static final ItemRegistry instance = new ItemRegistry();
	
	private static Map<Item, ArrayList<String>> variantNames = new HashMap<Item, ArrayList<String>>();
	
	public static ItemRegistry instance() {
		return instance;
	}
	
	protected void registerVariantNames(Item item, ArrayList<String> variants) {
		variantNames.put(item, variants);
	}
	
	public void insertItemVariantNames(Map<Item, List<String>> mapping) {
		mapping.putAll(variantNames);
	}
}
