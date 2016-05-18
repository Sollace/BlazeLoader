package com.blazeloader.event.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.event.handlers.EventHandler;

import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;

@Mixin(PlayerInteractionManager.class)
public abstract class MPlayerInteractionManager {
	@Inject(method = "tryHarvestBlock(Lnet/minecraft/util/math/BlockPos;)Z", at = @At("RETURN"))
	private void onTryHarvestBlock(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
		EventHandler.eventTryHarvestBlock((PlayerInteractionManager)(Object)this, info, pos);
	}
}
