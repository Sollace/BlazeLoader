package com.blazeloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.api.entity.properties.EntityPropertyManager;
import com.blazeloader.event.handlers.EventHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class MEntity implements ICommandSender {
	@Inject(method = "<init>(Lnet/minecraft/world/World;)V", at = @At("RETURN"))
	private void initEntity(World w, CallbackInfo info) {
		EventHandler.initEntity((Entity)(Object)this, w);
	}
	
	@Inject(method = "writeToNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("HEAD"))
	private void onWriteToNBT(NBTTagCompound tag, CallbackInfo info) {
		EntityPropertyManager.instance().writeToNBT((Entity)(Object)this, tag);
	}
	
	@Inject(method = "readFromNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("HEAD"))
	private void onReadFromNBT(NBTTagCompound tag, CallbackInfo info) {
		EntityPropertyManager.instance().readFromNBT((Entity)(Object)this, tag);
	}
	
	@Inject(method = "copyDataFromOld(Lnet/minecraft/entity/Entity;)V", at = @At("RETURN"))
	private void onCopyDataFromOld(Entity old, CallbackInfo info) {
		EntityPropertyManager.instance().copyToEntity(old, (Entity)(Object)this);
	}
	
	@Inject(method = "addEntityCrashInfo (Lnet/minecraft/crash/CrashReportCategory;)V", at = @At("RETURN"))
	private void onAddEntityCrashInfo(CrashReportCategory section, CallbackInfo info) {
    	EntityPropertyManager.instance().addEntityCrashInfo((Entity)(Object)this, section);
    }
	
	@Inject(method = "onUpdate()V", at = @At("RETURN"))
	private void onOnUpdate(CallbackInfo info) {
		EntityPropertyManager.instance().onEntityUpdate((Entity)(Object)this);
	}
	
	@Inject(method = "entityDropItem(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/item/EntityItem;", at = @At("HEAD"), cancellable = true)
	private void onEntityDropItem(ItemStack droppedItem, float yOffset, CallbackInfoReturnable<EntityItem> info) {
		EventHandler.eventEntityDropItem((Entity)(Object)this, info, droppedItem, yOffset);
	}
	
	@Inject(method = "doBlockCollisions()V", at = @At("HEAD"))
	private void onDoBlockCollisions(CallbackInfo info) {
		EventHandler.eventDoBlockCollisions(this);
	}
}
