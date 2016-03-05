package com.blazeloader.event.mixin.common.world;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blazeloader.event.handlers.EventHandler;

import net.minecraft.world.WorldServer;

@Mixin(WorldServer.class)
public abstract class MWorldServer {
	@Inject(method = "init()Lnet/minecraft/world/World;", at = @At(value = "RETURN", shift = Shift.BEFORE))
	private void onInit(CallbackInfo info) {
		EventHandler.eventInit((WorldServer)(Object)this);
	}
}
