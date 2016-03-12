package com.blazeloader.event.mixin.common;

import java.net.SocketAddress;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.event.handlers.EventHandler;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;

@Mixin(ServerConfigurationManager.class)
public abstract class MServerConfigurationManager {
	
	@Inject(method = "playerLoggedIn(Lnet/minecraft/entity/player/EntityPlayerMP;)V", at = @At("HEAD"))
	private void onPlayerLoggedIn(EntityPlayerMP player, CallbackInfo info) {
		EventHandler.eventPlayerLoggedIn((ServerConfigurationManager)(Object)this, player);
	}
	
	@Inject(method = "playerLoggedOut(Lnet/minecraft/entity/player/EntityPlayerMP;)V", at = @At("HEAD"))
	private void onPlayerLoggedOut(EntityPlayerMP player, CallbackInfo info) {
		EventHandler.eventPlayerLoggedOut((ServerConfigurationManager)(Object)this, player);
	}
	
	@Inject(method = "recreatePlayerEntity(Lnet/minecraft/entity/player/EntityPlayerMP;IZ)Lnet/minecraft/entity/player/EntityPlayerMP;", at = @At(value = "RETURN", shift = Shift.BEFORE))
	private void onRecreatePlayerEntity(EntityPlayerMP oldPlayer, int dimension, boolean didWin, CallbackInfoReturnable<EntityPlayerMP> info) {
		EventHandler.eventRecreatePlayerEntity((ServerConfigurationManager)(Object)this, oldPlayer, dimension, didWin);
	}
	
	@Inject(method = "allowUserToConnect(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Ljava/lang/String;", at = @At(value = "RETURN", shift = Shift.BEFORE), cancellable = true)
	private void onAllowUserToConnect(SocketAddress address, GameProfile profile, CallbackInfoReturnable<String> info) {
		EventHandler.eventAllowUserToConnect(info, address, profile);
	}
}
