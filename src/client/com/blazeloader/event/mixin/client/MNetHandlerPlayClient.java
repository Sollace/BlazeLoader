package com.blazeloader.event.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blazeloader.event.handlers.client.EventHandlerClient;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketOpenWindow;

@Mixin(NetHandlerPlayClient.class)
public abstract class MNetHandlerPlayClient implements INetHandlerPlayClient {
	@Inject(method = "handleOpenWindow(Lnet/minecraft/network/play/server/SPacketOpenWindow;)V", at = @At("HEAD"), cancellable = true)
	private void onHandleOpenWindow(SPacketOpenWindow packet, CallbackInfo info) {
		EventHandlerClient.eventHandleOpenWindow(this, info, packet);
	}
	
	@Inject(method = "handleHeldItemChange(Lnet/minecraft/network/play/server/SPacketHeldItemChange;)V", at = @At("HEAD"))
	private void onHandleHeldItemChange(SPacketHeldItemChange packet, CallbackInfo info) {
		EventHandlerClient.eventHandleHeldItemChange(info, packet);
	}
	
	@Inject(method = "handleCollectItem(Lnet/minecraft/network/play/server/SPacketCollectItem;)V", at = @At("HEAD"))
	private void onHandleCollectItem(SPacketCollectItem packet, CallbackInfo info) {
		EventHandlerClient.eventHandleCollectItem(packet);
	}
}
