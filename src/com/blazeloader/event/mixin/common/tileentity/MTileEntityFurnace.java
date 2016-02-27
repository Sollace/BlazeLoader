package com.blazeloader.event.mixin.common.tileentity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.event.handlers.InternalEventHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

@Mixin(TileEntityFurnace.class)
public abstract class MTileEntityFurnace {
	@Inject(method = "getItemBurnTime(Lnet/minecraft/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
	private void onGetItemBurnTime(ItemStack stack, CallbackInfoReturnable<Integer> info) {
		InternalEventHandler.eventGetItemBurnTime(info, stack);
	}
}
