package com.blazeloader.util.transformers.mapping;

import com.blazeloader.util.transformers.transformations.Transformation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Deprecated
public class GlobalTransformationMap extends LinkedList<Transformation> implements TransformationMap {
	
    @Override
    public List<Transformation> getTransformations(String className) {
        return getTransformations();
    }

    public List<Transformation> getTransformations() {
        return Collections.unmodifiableList(this);
    }

    @Override
    public int getNumTransformations(String className) {
        return size();
    }

    public int getNumTransformations() {
        return size();
    }

    @Override
    public void addTransformation(String className, Transformation transformation) {
        addTransformation(transformation);
    }

    public void addTransformation(Transformation transformation) {
        if (transformation == null) {
            throw new IllegalArgumentException("Cannot have a null transformation!");
        }
        add(transformation);
    }
}
