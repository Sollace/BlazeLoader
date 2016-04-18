package com.blazeloader.api.toolset;

import java.util.Set;

import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

/**
 * Base class for a tool. Can be used by mods to add their own types of tools.
 */
public abstract class Tool extends ItemTool implements ITool {
	private final ToolsetAttributes attributes;
	
	private final float damageValue;
	
	protected Tool(float attackDamage, ToolsetAttributes attributes, Set effectiveBlocks) {
		super(attackDamage, ToolMaterial.WOOD, effectiveBlocks);
		this.attributes = attributes;
		this.damageValue = attackDamage;
	}
	
	public ToolsetAttributes getToolAttributes() {
		return attributes;
	}
	
    @Override
    public int getItemEnchantability() {
        return getToolAttributes().getEnchantability();
    }

    @Override
    public String getToolMaterialName() {
        return getToolAttributes().toString();
    }

    @Override
    public boolean getIsRepairable(ItemStack repaired, ItemStack repairMaterial) {
        return getToolAttributes().getIsRepairable(repairMaterial);
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers() {
        return getToolAttributes().getAttributeModifiers(super.getItemAttributeModifiers(), null, damageValue, "Tool modifier");
    }
}
