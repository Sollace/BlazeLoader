package com.blazeloader.util.transformers.transformations;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public abstract class FieldTransformation extends Transformation {
    private final String fieldName;

    public FieldTransformation(String targetClass, String fieldName) {
        super(targetClass, "*".equals(fieldName));
        this.fieldName = fieldName;
    }
    
    @Override
    public final boolean apply(ClassNode cls) {
        String dotName = getDotName(cls.name);
        boolean didApply = false;
        if (dotName.equals(targetClass)) {
            for (FieldNode field : cls.fields) {
                if (isGlobal || field.name.equals(fieldName)) {
                	transformField(field);
                    didApply = true;
                }
            }
        }
        return didApply;
    }
    
    protected abstract void transformField(FieldNode field);
}
