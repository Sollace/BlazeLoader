package com.blazeloader.event.mixin.client.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blazeloader.event.handlers.client.EventHandlerClient;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;

@Mixin(EntityPlayerSP.class)
public abstract class MEntityPlayerSP extends AbstractClientPlayer {
	public MEntityPlayerSP() {super(null, null);}
	
	@Inject(method = "setPlayerSPHealth(F)V", at = @At("RETURN"))
	private void onSetPlayerSPHealth(float health, CallbackInfo info) {
		EventHandlerClient.eventSetPlayerSPHealth((EntityPlayerSP)(Object)this, health);
	}
}
