package com.blazeloader.event.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.event.handlers.InternalEventHandler;

import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.network.Packet;

@Mixin(EntityTrackerEntry.class)
public abstract class MEntityTrackerEntry {
	@Inject(method = "createSpawnPacket ()Lnet/minecraft/network/Packet;", at = @At("HEAD"))
	private void onGetSpawnPacket(CallbackInfoReturnable<Packet> info) {
		InternalEventHandler.eventGetSpawnPacket((EntityTrackerEntry)(Object)this, info);
	}
}
