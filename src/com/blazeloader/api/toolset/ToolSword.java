package com.blazeloader.api.toolset;

import com.google.common.collect.Multimap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class ToolSword extends ItemSword implements Tool {
    private final ToolsetAttributes attributes;

    private float damageValue = 4;

    public ToolSword(ToolsetAttributes material) {
        super(ToolMaterial.WOOD);
        attributes = material;
        setMaxDamage(material.getMaxUses());
        damageValue = material.getDamageVsEntity(4);
    }

	@Override
	public ToolsetAttributes getToolAttributes() {
		return attributes;
	}

    @Override
    public float getDamageVsEntity() {
        return attributes.getDamageVsEntity(0);
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
        return attributes.getAttributeModifiers(super.getItemAttributeModifiers(), null, damageValue, "Weapon modifier");
    }
}
