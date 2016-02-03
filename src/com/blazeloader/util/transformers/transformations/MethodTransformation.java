package com.blazeloader.util.transformers.transformations;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class MethodTransformation extends Transformation {
    public final String methodName;

    public MethodTransformation(String targetClass, String methodName) {
        super(targetClass, "*".equals(methodName));
        this.methodName = methodName;
    }
    
    @Override
    public final boolean apply(ClassNode cls) {
        String dotName = getDotName(cls.name);
        boolean didApply = false;
        if (dotName.equals(targetClass)) {
            for (MethodNode method : cls.methods) {
                String mName = method.name.concat(" ").concat(method.desc).replace('/', '.');
                if (isGlobal || mName.equals(methodName)) {
                	transformMethod(method);
                    didApply = true;
                }
            }
        }
        return didApply;
    }
    
    protected abstract void transformMethod(MethodNode method);
}
