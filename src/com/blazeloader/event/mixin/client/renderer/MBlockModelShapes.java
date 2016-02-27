package com.blazeloader.event.mixin.client.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.event.handlers.client.InternalEventHandlerClient;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@Mixin(BlockModelShapes.class)
public abstract class MBlockModelShapes {
	@Inject(method = "getTexture(Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", at = @At("HEAD"), cancellable = true)
	private void onGetTexture(IBlockState state, CallbackInfoReturnable<TextureAtlasSprite> info) {
		InternalEventHandlerClient.eventGetTexture((BlockModelShapes)(Object)this, info, state);
	}
}
