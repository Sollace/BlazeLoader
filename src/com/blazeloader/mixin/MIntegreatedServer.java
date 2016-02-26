package com.blazeloader.mixin;

import java.io.File;
import java.net.Proxy;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.event.handlers.InternalEventHandler;

import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;

@Mixin(IntegratedServer.class)
public abstract class MIntegreatedServer extends MinecraftServer {
	public MIntegreatedServer(Proxy proxy, File workDir) {
		super(proxy, workDir);
	}
	
	@Inject(method = "createNewCommandManager()Lnet/minecraft/command/ServerCommandManager;", at = @At(value = "RETURN", shift = Shift.BEFORE), cancellable = true)
	private void onCreateNewCommandManager(CallbackInfoReturnable<CommandHandler> info) {
		InternalEventHandler.eventCreateNewCommandManager(info);
	}
}
