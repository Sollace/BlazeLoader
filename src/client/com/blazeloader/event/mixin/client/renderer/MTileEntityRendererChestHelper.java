package com.blazeloader.event.mixin.client.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blazeloader.event.handlers.client.InternalEventHandlerClient;

import net.minecraft.client.renderer.tileentity.TileEntityRendererChestHelper;
import net.minecraft.item.ItemStack;

@Mixin(TileEntityRendererChestHelper.class)
public abstract class MTileEntityRendererChestHelper {
	@Inject(method = "renderByItem(Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
	private void onRenderByItem(ItemStack stack, CallbackInfo info) {
		InternalEventHandlerClient.eventRenderByItem(info, stack);
	}
}
