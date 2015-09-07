package com.blazeloader.api.toolset;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class ToolsetAttributes {
	
	private static final HashMap<ToolMaterial, ToolsetAttributes> conversionMapping = new HashMap<ToolMaterial, ToolsetAttributes>();
	
    public static final ToolsetAttributes
            WOOD = new ToolsetAttributes(ToolMaterial.WOOD),
            STONE = new ToolsetAttributes(ToolMaterial.STONE),
            IRON = new ToolsetAttributes(ToolMaterial.IRON),
            EMERALD = new ToolsetAttributes(ToolMaterial.EMERALD),
            GOLD = new ToolsetAttributes(ToolMaterial.GOLD);

    private final String string;
    private final int hl;
    private final int uses;
    private final float efficiencyOnProperMaterial;
    private final float damageVsEntity;
    private final int enchant;
    
    private final Item item;
    private final int data;

    public ToolsetAttributes(String name, Item materialItem, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        string = name;
        hl = harvestLevel;
        uses = maxUses;
        efficiencyOnProperMaterial = efficiency;
        damageVsEntity = damage;
        enchant = enchantability;
        item = materialItem;
        data = -1;
    }
    
    public ToolsetAttributes(String name, Block materialItem, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        string = name;
        hl = harvestLevel;
        uses = maxUses;
        efficiencyOnProperMaterial = efficiency;
        damageVsEntity = damage;
        enchant = enchantability;
        item = Item.getItemFromBlock(materialItem);
        data = -1;
    }
    
    public ToolsetAttributes(String name, ItemStack materialItem, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        string = name;
        hl = harvestLevel;
        uses = maxUses;
        efficiencyOnProperMaterial = efficiency;
        damageVsEntity = damage;
        enchant = enchantability;
        item = materialItem.getItem();
        data = materialItem.getItemDamage();
    }
    
    private ToolsetAttributes(ToolMaterial material) {
    	string = material.name();
    	hl = material.getHarvestLevel();
    	uses = material.getMaxUses();
    	efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
    	damageVsEntity = material.getDamageVsEntity();
    	enchant = material.getEnchantability();
    	item = material.getRepairItem();
    	data = -1;
    	conversionMapping.put(material, this);
    }

    public int getMaxUses() {
        return uses;
    }

    public float getEfficiencyOnProperMaterial() {
        return efficiencyOnProperMaterial;
    }

    public float getDamageVsEntity(float offset) {
        return offset + damageVsEntity;
    }

    public int getHarvestLevel() {
        return hl;
    }

    public int getEnchantability() {
        return enchant;
    }

    public Item getItem() {
        return item;
    }

    public boolean getIsRepairable(ItemStack stack) {
        return getItem() == stack.getItem() && (data < 0 || data == stack.getItemDamage());
    }

    public Multimap getAttributeModifiers(Multimap map, UUID attr, double damage, String name) {
        map.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(attr, name, damage, 0));
        return map;
    }

    public String toString() {
        return string;
    }
    
    /**
     * Converts the given vanilla tool material to an instance of ToolsetAttributes.
     */
    public static ToolsetAttributes fromEnum(ToolMaterial toolset) {
    	if (conversionMapping.containsKey(toolset)) {
    		return conversionMapping.get(toolset);
    	}
    	return new ToolsetAttributes(toolset);
    }
}
