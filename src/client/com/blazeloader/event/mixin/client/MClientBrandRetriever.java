package com.blazeloader.event.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.event.handlers.InternalEventHandler;

import net.minecraft.client.ClientBrandRetriever;

@Mixin(ClientBrandRetriever.class)
public abstract class MClientBrandRetriever {
	@Inject(method = "getClientModName()Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
	public void onGetClientBrandName(CallbackInfoReturnable<String> info) {
		InternalEventHandler.eventGetModName(info);
	}
}
