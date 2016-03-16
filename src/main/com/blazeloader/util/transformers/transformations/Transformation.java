package com.blazeloader.util.transformers.transformations;

import com.blazeloader.bl.obf.AccessLevel;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.*;

public abstract class Transformation {
	private final TargetSelector targeter;
    public final String targetClass;

    public Transformation(TargetSelector selector, String target) {
    	targeter = selector.initWith(this);
        targetClass = target;
    }
    
    protected final int setPublicity(int currAccess, AccessLevel publicity) {
        int pubValue = publicity.getValue();
        int ret = (currAccess & ~7);
        switch (currAccess & 7) {
            case ACC_PRIVATE:
                ret |= pubValue;
                break;
            case 0:
                ret |= (pubValue != ACC_PRIVATE ? pubValue : 0);
                break;
            case ACC_PROTECTED:
                ret |= (pubValue != ACC_PRIVATE && pubValue != 0 ? pubValue : ACC_PROTECTED);
                break;
            case ACC_PUBLIC:
                ret |= (pubValue != ACC_PRIVATE && pubValue != 0 && pubValue != ACC_PROTECTED ? pubValue : ACC_PUBLIC);
                break;
            default:
                throw new IllegalArgumentException("Non-existent access mode!");
        }
        return ret;
    }
    
    protected final int setFinality(int currAccess, boolean finalValue) {
    	if (finalValue) {
    		currAccess |= ACC_FINAL;
        } else {
        	currAccess &= ~ACC_FINAL;
        }
    	return currAccess;
    }

    public static String getDotName(String slashName) {
        return slashName.replace('/', '.');
    }
    
    public boolean apply(ClassNode cls) {
    	String dotName = Transformation.getDotName(cls.name);
    	if (dotName.equals(targetClass)) return targeter.match(cls);
    	return false;
    }
    
    public abstract void transformField(FieldNode node);
    
    public abstract void transformMethod(MethodNode node);
}
