package com.blazeloader.util.transformers.mapping;

import com.blazeloader.util.transformers.transformations.Transformation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DefaultTransformationMap extends HashMap<String, List<Transformation>> implements TransformationMap {
    
    public List<Transformation> getTransformations(String className) {
        if (className == null) {
            throw new IllegalArgumentException("Class name must not be null!");
        }
        List<Transformation> trans = get(className);
        if (trans == null) {
            trans = new LinkedList<Transformation>();
            put(className, trans);
        }
        return trans;
    }
    
    public int getNumTransformations(String className) {
        return getTransformations(className).size();
    }
    
    public void addTransformation(String className, Transformation transformation) {
        if (className == null || transformation == null) {
            throw new IllegalArgumentException("Class name and transformation must not be null!");
        }
        getTransformations(className).add(transformation);
    }
}
