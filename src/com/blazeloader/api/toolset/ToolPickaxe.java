package com.blazeloader.api.toolset;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

public class ToolPickaxe extends ItemPickaxe implements Tool {
    private final ToolsetAttributes attributes;

    private float damageValue = 4;

    public ToolPickaxe(ToolsetAttributes material) {
        super(ToolMaterial.WOOD);
        attributes = material;
        super.setMaxDamage(material.getMaxUses());
        efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
        damageValue = material.getDamageVsEntity(2);
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
    public boolean getIsRepairable(ItemStack repairedItem, ItemStack repairMaterial) {
        return attributes.getIsRepairable(repairMaterial);
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        return attributes.getAttributeModifiers(super.getItemAttributeModifiers(), null, damageValue, "Tool modifier");
    }

    @Override
    public boolean canHarvestBlock(Block block) {
        return block == Blocks.obsidian ? attributes.getHarvestLevel() == 3 : (block != Blocks.diamond_block && block != Blocks.diamond_ore ? (block != Blocks.emerald_ore && block != Blocks.emerald_block ? (block != Blocks.gold_block && block != Blocks.gold_ore ? (block != Blocks.iron_block && block != Blocks.iron_ore ? (block != Blocks.lapis_block && block != Blocks.lapis_ore ? (block != Blocks.redstone_ore && block != Blocks.lit_redstone_ore ? (block.getMaterial() == Material.rock || (block.getMaterial() == Material.iron || block.getMaterial() == Material.anvil)) : attributes.getHarvestLevel() >= 2) : attributes.getHarvestLevel() >= 1) : attributes.getHarvestLevel() >= 1) : attributes.getHarvestLevel() >= 2) : attributes.getHarvestLevel() >= 2) : attributes.getHarvestLevel() >= 2);
    }
}
