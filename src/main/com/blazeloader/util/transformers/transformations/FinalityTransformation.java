package com.blazeloader.util.transformers.transformations;

import static org.objectweb.asm.Opcodes.ACC_FINAL;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class FinalityTransformation extends Transformation {
	
	private final boolean finalValue;
	
	public FinalityTransformation(TargetSelector selector, String targetClass, boolean setFinalTo) {
		super(selector, targetClass);
		finalValue = setFinalTo;
	}

	@Override
	public void transformField(FieldNode node) {
		node.access = setFinality(node.access, finalValue);
	}

	@Override
	public void transformMethod(MethodNode node) {
		node.access = setFinality(node.access, finalValue);
	}
	
    protected final int setFinality(int currAccess, boolean finalValue) {
    	if (finalValue) {
    		currAccess |= ACC_FINAL;
        } else {
        	currAccess &= ~ACC_FINAL;
        }
    	return currAccess;
    }
}
