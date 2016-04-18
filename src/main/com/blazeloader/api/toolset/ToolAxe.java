package com.blazeloader.api.toolset;

import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;


public class ToolAxe extends ItemAxe implements ITool {
    private final ToolsetAttributes attributes;

    private float damageValue = 3;

    public ToolAxe(ToolsetAttributes material) {
        super(ToolMaterial.WOOD);
        attributes = material;
        super.setMaxDamage(material.getMaxUses());
        efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
        damageValue = material.getDamageVsEntity(3);
    }
    
	@Override
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
    public Multimap<String, AttributeModifier> getItemAttributeModifiers() {
        return attributes.getAttributeModifiers(super.getItemAttributeModifiers(), null, damageValue, "Tool modifier");
    }
}
