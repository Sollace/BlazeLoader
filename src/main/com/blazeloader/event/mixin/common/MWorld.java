package com.blazeloader.event.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.bl.interop.ForgeWorldAccess;
import com.blazeloader.event.handlers.EventHandler;
import com.blazeloader.event.handlers.InternalEventHandler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class MWorld implements ForgeWorldAccess {
	
	public double MAX_ENTITY_RADIUS;
	
	@Override
	public double getMaxEntitySize(double def) {
		return MAX_ENTITY_RADIUS;
	}
	
	@Override
	public void setMaxEntitySize(double size) {
		MAX_ENTITY_RADIUS = size;
	}
	
	@Inject(method = "setBlockState(Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", at = @At("HEAD"), cancellable = true)
	private void onSetBlockState(BlockPos pos, IBlockState state, int flag, CallbackInfoReturnable<Boolean> info) {
		EventHandler.eventSetBlockState(info, (World)(Object)this, pos, state);
	}
	
	@Inject(method = "spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
	private void onSpawnEntityInWorld(Entity entity, CallbackInfoReturnable<Boolean> info) {
		EventHandler.eventSpawnEntityInWorld(info, (World)(Object)this, entity);
	}
	
	@Inject(method = "doesBlockHaveSolidTopSurface(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)Z", at = @At("RETURN"), cancellable = true)
	private static void internalDoesBlockHaveSolidTopSurface(IBlockAccess access, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
		InternalEventHandler.eventDoesBlockHaveSolidTopSurface(info, access, pos);
	}
	
	@Inject(method = "onEntityRemoved(Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"))
	private void internalOnEntityRemoved(Entity entity, CallbackInfo info) {
		InternalEventHandler.eventOnEntityRemoved(entity);
	}
	
	@Inject(method = "canBlockFreeze(Lnet/minecraft/util/BlockPos;Z)Z", at = @At("HEAD"), cancellable = true)
	private void internalCanBlockFreeze(BlockPos pos, boolean noWaterAdj, CallbackInfoReturnable<Boolean> info) {
		InternalEventHandler.eventCanBlockFreeze((World)(Object)this, pos, noWaterAdj, info);
	}
}
