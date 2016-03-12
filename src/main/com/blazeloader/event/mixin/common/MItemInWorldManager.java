package com.blazeloader.event.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.event.handlers.EventHandler;

import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.BlockPos;

@Mixin(ItemInWorldManager.class)
public abstract class MItemInWorldManager {
	@Inject(method = "tryHarvestBlock (Lnet/minecraft/util/BlockPos;)Z", at = @At("RETURN"))
	private void onTryHarvestBlock(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
		EventHandler.eventTryHarvestBlock((ItemInWorldManager)(Object)this, info, pos);
	}
}
