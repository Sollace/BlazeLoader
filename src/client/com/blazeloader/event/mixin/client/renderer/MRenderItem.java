package com.blazeloader.event.mixin.client.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blazeloader.event.handlers.client.InternalEventHandlerClient;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;

@Mixin(RenderItem.class)
public abstract class MRenderItem implements IResourceManagerReloadListener {
	@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resources/model/IBakedModel;)V", at = @At("HEAD"))
	private void onRenderItem(ItemStack stack, IBakedModel model, CallbackInfo info) {
		InternalEventHandlerClient.eventRenderItem(info, stack, model);
	}
}
