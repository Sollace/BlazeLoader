package com.blazeloader.util.version.type;

import com.blazeloader.util.version.BuildType;
import com.blazeloader.util.version.SaveableVersion;

/**
 * Basic implementation of a version. Supports saving and loading from a config file and can have only two components.
 */
public class DoubleVersion extends SaveableVersion<DoubleVersion> {
    public DoubleVersion(String id, String name, BuildType buildType, int version1, int version2) {
        super(id, name, buildType, version1, version2);
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
    
    protected DoubleVersion createVersion(String id, String name, BuildType buildType, int... parts) {
    	return new DoubleVersion(id, name, buildType, parts[0], parts[1]);
    }
}
