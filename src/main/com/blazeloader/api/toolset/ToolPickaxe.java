package com.blazeloader.api.toolset;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

public class ToolPickaxe extends ItemPickaxe implements ITool {
    private final ToolsetAttributes attributes;
    
    public ToolPickaxe(ToolsetAttributes material) {
        super(ToolMaterial.WOOD);
        attributes = material;
        super.setMaxDamage(material.getMaxUses());
        efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
        damageVsEntity = material.getDamageVsEntity(1);
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
    public Multimap getItemAttributeModifiers(EntityEquipmentSlot slot) {
    	return attributes.getAttributeModifiers(slot, super.getItemAttributeModifiers(slot), ATTACK_DAMAGE_MODIFIER, ATTACK_SPEED_MODIFIER, damageVsEntity, attackSpeed, "Tool modifier");
    }

    @Override
    public boolean canHarvestBlock(IBlockState state) {
    	Block block = state.getBlock();
        if (block == Blocks.obsidian) return attributes.getHarvestLevel() == 3;
        if (block == Blocks.iron_block || block == Blocks.iron_ore || block == Blocks.lapis_block || block == Blocks.lapis_ore) {
        	return attributes.getHarvestLevel() > 0;
        }
        if (block == Blocks.redstone_ore || block == Blocks.lit_redstone_ore) {
        	Material material = state.getMaterial();
            return material == Material.rock  || material == Material.iron || material == Material.anvil;
        }
        return attributes.getHarvestLevel() > 1;
    }
}
