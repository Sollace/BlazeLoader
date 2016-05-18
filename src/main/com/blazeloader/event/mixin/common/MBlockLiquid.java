package com.blazeloader.event.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.event.handlers.InternalEventHandler;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

@Mixin(BlockLiquid.class)
public abstract class MBlockLiquid {
	@Inject(method = "getFlowDirection(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/material/Material;)F", at = @At("HEAD"))
	private static void iternalGetFlowDirection(IBlockAccess w, BlockPos pos, Material material, CallbackInfoReturnable<Float> info) {
		InternalEventHandler.eventGetFlowDirection(w, pos, info);
	}
}
