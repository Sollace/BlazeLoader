package com.blazeloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blazeloader.event.handlers.EventHandler;

import net.minecraft.network.play.server.S0DPacketCollectItem;

@Mixin(S0DPacketCollectItem.class)
public abstract class MS0DPacketCollectItem {
	@Inject(method = "<init>(II)V", at = @At("RETURN"))
	private void initS0DPacketCollectItem(int itemId, int entityId, CallbackInfo info) {
		EventHandler.initS0DPacketCollectItem(itemId, entityId);
	}
}
