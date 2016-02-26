package org.spongepowered.asm.mixin.injection.callback;

public interface CallbackInfoReturnable<R> extends CallbackInfo {
    public void setReturnValue(R returnValue) throws CancellationException;
    public R getReturnValue();
}