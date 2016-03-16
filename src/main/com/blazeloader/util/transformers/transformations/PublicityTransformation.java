package com.blazeloader.util.transformers.transformations;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.blazeloader.bl.obf.AccessLevel;

public class PublicityTransformation extends Transformation {
	public final AccessLevel access;
	
	public PublicityTransformation(TargetSelector selector, String targetClass, AccessLevel level) {
		super(selector, targetClass);
		access = level;
	}

	@Override
	public void transformField(FieldNode node) {
		node.access = setPublicity(node.access, access);
	}

	@Override
	public void transformMethod(MethodNode node) {
		node.access = setPublicity(node.access, access);
	}
}
