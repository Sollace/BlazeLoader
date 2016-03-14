package com.blazeloader.event.mixin.common;

import java.io.File;
import java.net.Proxy;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.event.handlers.InternalEventHandler;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixer;

@Mixin(IntegratedServer.class)
public abstract class MIntegratedServer extends MinecraftServer {
	
	public MIntegratedServer(File anvilFileIn, Proxy proxyIn, DataFixer dataFixerIn,
			YggdrasilAuthenticationService authServiceIn, MinecraftSessionService sessionServiceIn,
			GameProfileRepository profileRepoIn, PlayerProfileCache profileCacheIn) {
		super(anvilFileIn, proxyIn, dataFixerIn, authServiceIn, sessionServiceIn, profileRepoIn, profileCacheIn);
	}

	@Inject(method = "createNewCommandManager()Lnet/minecraft/command/ServerCommandManager;", at = @At(value = "RETURN", shift = Shift.BEFORE), cancellable = true)
	private void onCreateNewCommandManager(CallbackInfoReturnable<CommandHandler> info) {
		InternalEventHandler.eventCreateNewCommandManager((MinecraftServer)(Object)this, info);
	}
}
