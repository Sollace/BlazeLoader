package com.blazeloader.api.toolset;

import java.util.Set;

import com.google.common.collect.Multimap;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

/**
 * Base class for a tool. Can be used by mods to add their own types of tools.
 */
public abstract class ToolBase extends ItemTool implements Tool {
	
	private final ToolsetAttributes attributes;
	
	private final float damageValue;
	
	protected ToolBase(float attackDamage, ToolsetAttributes attributes, Set effectiveBlocks) {
		super(attackDamage, ToolMaterial.WOOD, effectiveBlocks);
		this.attributes = attributes;
		this.damageValue = attackDamage;
	}
	
	public ToolsetAttributes getToolAttributes() {
		return attributes;
	}
	
    @Override
    public int getItemEnchantability() {
        return attributes.getEnchantability();
    }

    @Override
    public String getToolMaterialName() {
        return attributes.toString();
    }

    @Override
    public boolean getIsRepairable(ItemStack repaired, ItemStack repairMaterial) {
        return attributes.getIsRepairable(repairMaterial);
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        return attributes.getAttributeModifiers(super.getItemAttributeModifiers(), null, damageValue, "Tool modifier");
    }
}
