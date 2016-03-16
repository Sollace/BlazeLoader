package com.blazeloader.util.transformers.transformations;

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
	
}
