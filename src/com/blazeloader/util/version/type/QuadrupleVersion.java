package com.blazeloader.util.version.type;

import com.blazeloader.util.version.BuildType;
import com.blazeloader.util.version.SaveableVersion;

/**
 * Basic implementation of a version. Supports saving and loading from a config file and can have only four components.
 */
public class QuadrupleVersion extends SaveableVersion<QuadrupleVersion> {
	
    public QuadrupleVersion(String id, String name, BuildType buildType, int version1, int version2, int version3, int version4) {
        super(id, name, buildType, version1, version2, version3, version4);
    }
    
    /**
     * Gets the first component.
     */
    public int getFirst() {
        return getNthComponent(0);
    }
    
    /**
     * Gets the second component.
     */
    public int getSecond() {
        return getNthComponent(1);
    }
    
    /**
     * Gets the third component.
     */
    public int getThird() {
        return getNthComponent(2);
    }
    
    /**
     * Gets the fourth component.
     */
    public int getFourth() {
        return getNthComponent(3);
    }
    
    protected QuadrupleVersion createVersion(String id, String name, BuildType buildType, int... parts) {
    	return new QuadrupleVersion(id, name, buildType, parts[0], parts[1], parts[2], parts[3]);
    }
}
