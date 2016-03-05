package com.blazeloader.util.transformers.transformations;

import com.blazeloader.bl.obf.AccessLevel;

import org.objectweb.asm.tree.FieldNode;

public class FieldPublicTransformation extends FieldTransformation {
    public final AccessLevel access;

    public FieldPublicTransformation(String targetClass, String fieldName, AccessLevel access) {
        super(targetClass, fieldName);
        this.access = access;
    }

	@Override
	protected void transformField(FieldNode field) {
		//BLMain.LOGGER_MAIN.logDebug("FieldPublicTransformation:" + dotName + "." + field.name + "\r\n\tTarget: " + fieldName);
        field.access = setAccess(field.access, access);
	}
}
