package com.blazeloader.util.transformers.mapping;

import com.blazeloader.util.transformers.transformations.Transformation;

import java.util.List;

public interface TransformationMap {
	
    public List<Transformation> getTransformations(String className);
    
    public int getNumTransformations(String className);
    
    public void addTransformation(String className, Transformation transformation);
}
