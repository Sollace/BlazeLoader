package com.blazeloader.util.transformers.transformations;

import com.blazeloader.bl.obf.AccessLevel;

import org.objectweb.asm.tree.FieldNode;

public class FieldFinalTransformation extends FieldTransformation {
    private final boolean finalValue;

    public FieldFinalTransformation(String targetClass, String fieldName, boolean setFinalTo) {
        super(targetClass, fieldName);
        finalValue = setFinalTo;
    }
    
	@Override
	protected void transformField(FieldNode field) {
    	//BLMain.LOGGER_MAIN.logDebug("FieldFinalTransformation:" + dotName + "." + field.name + "\r\n\tTarget: " + fieldName);
        field.access = setAccess(field.access, AccessLevel.PUBLIC, true, finalValue);
	}
}
