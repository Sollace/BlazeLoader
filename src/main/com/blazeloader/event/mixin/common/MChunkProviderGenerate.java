package com.blazeloader.event.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.blazeloader.event.handlers.InternalEventHandler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.ChunkProviderOverworld;

@Mixin(ChunkProviderOverworld.class)
public abstract class MChunkProviderGenerate implements IChunkGenerator {
	@Shadow
	private World worldObj;
	
	@ModifyArg(
		method = "populate(II)V",
		index = 1,
		at = @At(
			value = "INVOKE",
			target = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"
		)
	)
	private IBlockState internalSetBlockState(BlockPos pos, IBlockState state, int flag) {
		return InternalEventHandler.getFluidFrozenState(worldObj, pos, state);
	}
}
