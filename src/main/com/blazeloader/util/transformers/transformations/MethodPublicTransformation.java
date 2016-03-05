package com.blazeloader.util.transformers.transformations;

import com.blazeloader.bl.obf.AccessLevel;

import org.objectweb.asm.tree.MethodNode;

public class MethodPublicTransformation extends MethodTransformation {
    private final AccessLevel accessLevel;

    public MethodPublicTransformation(String targetClass, String methodName, AccessLevel access) {
        super(targetClass, methodName);
        accessLevel = access;
    }

	@Override
	protected void transformMethod(MethodNode method) {
    	//BLMain.LOGGER_MAIN.logDebug("MethodPublicTransformation:" + dotName + "." + method.name + "\r\n\tTarget: " + methodName);
    	method.access = setAccess(method.access, accessLevel);
	}
}
