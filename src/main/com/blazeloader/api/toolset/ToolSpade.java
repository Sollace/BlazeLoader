package com.blazeloader.api.toolset;

import com.google.common.collect.Multimap;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;

public class ToolSpade extends ItemSpade implements Tool {
    private final ToolsetAttributes attributes;

    private float damageValue = 4;

    public ToolSpade(ToolsetAttributes material) {
        super(ToolMaterial.WOOD);
        attributes = material;
        super.setMaxDamage(material.getMaxUses());
        efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
        damageValue = material.getDamageVsEntity(1);
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
    public Multimap getItemAttributeModifiers() {
        return attributes.getAttributeModifiers(super.getItemAttributeModifiers(), null, damageValue, "Tool modifier");
    }
}
