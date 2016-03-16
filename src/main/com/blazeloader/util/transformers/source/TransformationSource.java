package com.blazeloader.util.transformers.source;

import com.blazeloader.util.transformers.BLAccessTransformer;

@Deprecated
public interface TransformationSource {
    public void provideTransformations(BLAccessTransformer transformer);
}
