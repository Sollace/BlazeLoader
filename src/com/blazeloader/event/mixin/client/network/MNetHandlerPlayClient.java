package com.blazeloader.event.mixin.client.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blazeloader.event.handlers.client.EventHandlerClient;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

@Mixin(NetHandlerPlayClient.class)
public abstract class MNetHandlerPlayClient implements INetHandlerPlayClient {
	@Inject(method = "handleOpenWindow(Lnet/minecraft/network/play/server/S2DPacketOpenWindow;)V", at = @At("HEAD"), cancellable = true)
	private void onHandleOpenWindow(S2DPacketOpenWindow packet, CallbackInfo info) {
		EventHandlerClient.eventHandleOpenWindow(this, info, packet);
	}
	
	@Inject(method = "handleHeldItemChange(Lnet/minecraft/network/play/server/S09PacketHeldItemChange;)V", at = @At("HEAD"))
	private void onHandleHeldItemChange(S09PacketHeldItemChange packet, CallbackInfo info) {
		EventHandlerClient.eventHandleHeldItemChange(info, packet);
	}
	
	@Inject(method = "handleCollectItem(Lnet/minecraft/network/play/server/S0DPacketCollectItem;)V", at = @At("HEAD"))
	private void onHandleCollectItem(S0DPacketCollectItem packet, CallbackInfo info) {
		EventHandlerClient.eventHandleCollectItem(packet);
	}
}
