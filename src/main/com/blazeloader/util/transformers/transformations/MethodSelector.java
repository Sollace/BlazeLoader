package com.blazeloader.util.transformers.transformations;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodSelector implements TargetSelector {
	private Transformation transformation;
	
	private final String methodName;
	private final boolean global;
	
	public MethodSelector(String name) {
		global = "*".equals(name);
		methodName = name;
	}
	
	public TargetSelector initWith(Transformation transformation) {
		this.transformation = transformation;
		return this;
	}
	
	public boolean match(ClassNode cls) {
        boolean didApply = false;
        for (MethodNode method : cls.methods) {
            if (global || method.name.equals(methodName)) {
            	transformation.transformMethod(method);
                didApply = true;
            }
        }
        return didApply;
    }
}
