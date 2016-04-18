package com.blazeloader.event.mixin.client;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blazeloader.api.item.ItemRegistry;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.item.Item;

@Mixin(ModelBakery.class)
public abstract class MModelBakery {
	@Shadow
	private Map<Item, List<String>> variantNames;
	
	@Inject(method = "registerVariantNames()V", at = @At("RETURN"))
	private void onRegisterVarientNames(CallbackInfo info) {
		ItemRegistry.instance().insertItemVariantNames(variantNames);
	}
}
