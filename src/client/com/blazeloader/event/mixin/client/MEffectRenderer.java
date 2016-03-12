package com.blazeloader.event.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.api.particles.ParticlesRegister;
import com.blazeloader.event.handlers.client.EventHandlerClient;

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;

@Mixin(EffectRenderer.class)
public abstract class MEffectRenderer {
	
	@Shadow
	private Map particleTypes;
	
	@Inject(method = "spawnEffectParticle(IDDDDDD[I)Lnet/minecraft/client/particle/EntityFX;", at = @At("HEAD"), cancellable = true)
	private void onSPawnEffectParticle(int particleId, double x, double y, double z, double xOffset, double yOffset, double zOffset, int[] args, CallbackInfoReturnable<EntityFX> info) {
		EventHandlerClient.eventSpawnEffectParticle((EffectRenderer)(Object)this, info, particleId, x, y, z, xOffset, yOffset, zOffset, args);
	}
	
	@Inject(method = "registerVanillaParticles()V", at = @At("RETURN"))
	private void onRegisterVanillaParticles(CallbackInfo info) {
		particleTypes = ParticlesRegister.instance().init(particleTypes);
	}
}
