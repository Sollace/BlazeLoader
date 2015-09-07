package com.blazeloader.api.toolset;

import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

public interface Tool {
	
	public static boolean isTool(Item item) {
		return item != null && (item instanceof Tool || item instanceof ItemTool);
	}
	
	public static boolean isTool(ItemStack stack) {
		return stack != null && isTool(stack.getItem());
	}
	
	public static boolean isTool(Entity entity) {
		return entity instanceof EntityItem && isTool(((EntityItem)entity).getEntityItem());
	}
	
	public ToolsetAttributes getToolAttributes();
    
    public int getItemEnchantability();
    
    public String getToolMaterialName();
    
    public boolean getIsRepairable(ItemStack repaired, ItemStack repairMaterial);
    
    public Multimap getItemAttributeModifiers();
}
