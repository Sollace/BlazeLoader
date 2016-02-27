package com.blazeloader.event.mixin.common.profiler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blazeloader.event.handlers.EventHandler;

import net.minecraft.profiler.Profiler;

@Mixin(Profiler.class)
public abstract class MProfiler {
	@Inject(method = "startSection(Ljava/lang/String;)V", at = @At("HEAD"))
	private void onStartSection(String name, CallbackInfo info) {
		EventHandler.eventStartSection((Profiler)(Object)this, name);
	}
	
	@Inject(method = "endSection()V", at = @At("HEAD"))
	private void onEndSection(CallbackInfo info) {
		EventHandler.eventEndSection((Profiler)(Object)this);
	}
}
