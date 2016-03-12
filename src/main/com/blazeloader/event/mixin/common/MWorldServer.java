package com.blazeloader.event.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.event.handlers.EventHandler;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

@Mixin(WorldServer.class)
public abstract class MWorldServer {
	@Inject(method = "init ()Lnet/minecraft/world/World;", at = @At(value = "RETURN"))
	private void onInit(CallbackInfoReturnable<World> info) {
		EventHandler.eventInit((WorldServer)(Object)this);
	}
}
