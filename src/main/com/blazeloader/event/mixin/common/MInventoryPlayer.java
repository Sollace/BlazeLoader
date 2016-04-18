package com.blazeloader.event.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blazeloader.event.handlers.EventHandler;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;

@Mixin(InventoryPlayer.class)
public abstract class MInventoryPlayer implements IInventory {
	@Inject(method = "changeCurrentItem(I)V", at = @At("HEAD"))
	private void onchangeCurrentItem(int increment, CallbackInfo info) {
		EventHandler.eventChangeCurrentItem((InventoryPlayer)(Object)this, info, increment);
	}
	
	@Inject(method = "setCurrentItem(Lnet/minecraft/item/Item;IZZ)V", at = @At("RETURN"))
	private void onSetCurrentItem(Item itemIn, int targetEntityId, boolean hasSubTypes, boolean isCreativeMode, CallbackInfo info) {
		EventHandler.eventSetCurrentItem((InventoryPlayer)(Object)this, itemIn, targetEntityId, hasSubTypes, isCreativeMode);
	}
}
