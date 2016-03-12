package com.blazeloader.util.transformers.transformations;

import org.objectweb.asm.tree.MethodNode;

import com.blazeloader.bl.obf.AccessLevel;

public class MethodFinalTransformation extends MethodTransformation {
    private final boolean finalValue;

    public MethodFinalTransformation(String targetClass, String methodName, boolean setFinalTo) {
        super(targetClass, methodName);
        finalValue = setFinalTo;
    }

	@Override
	protected void transformMethod(MethodNode method) {
		//BLMain.LOGGER_MAIN.logDebug("MethodFinalTransformation:" + dotName + "." + method.name + "\r\n\tTarget: " + methodName);
    	method.access = setAccess(method.access, AccessLevel.PUBLIC, true, finalValue);
	}
}
