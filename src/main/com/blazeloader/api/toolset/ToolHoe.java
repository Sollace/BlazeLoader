package com.blazeloader.api.toolset;

import net.minecraft.item.ItemHoe;

public class ToolHoe extends ItemHoe implements Tool {
    private final ToolsetAttributes attributes;

    public ToolHoe(ToolsetAttributes material) {
        super(ToolMaterial.WOOD);
        attributes = material;
        super.setMaxDamage(material.getMaxUses());
    }

	@Override
	public ToolsetAttributes getToolAttributes() {
		return attributes;
	}

    @Override
    public String getMaterialName() {
        return attributes.toString();
    }

	@Override
	public String getToolMaterialName() {
		return getMaterialName();
	}
}
