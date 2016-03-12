package com.blazeloader.util.version.type;

import com.blazeloader.util.version.BuildType;
import com.blazeloader.util.version.SaveableVersion;

/**
 * Specialised implementation of a version. Supports saving and loading from a config file and can have only three components.
 */
public class TripleVersion extends SaveableVersion<TripleVersion> {
	
    public TripleVersion(String id, String name, BuildType buildType, int version1, int version2, int version3) {
        super(id, name, buildType, version1, version2, version3);
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
    
    protected TripleVersion createVersion(String id, String name, BuildType buildType, int... parts) {
    	return new TripleVersion(id, name, buildType, parts[0], parts[1], parts[2]);
    }
}
