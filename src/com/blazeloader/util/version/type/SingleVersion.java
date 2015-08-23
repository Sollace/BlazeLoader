package com.blazeloader.util.version.type;

import com.blazeloader.util.version.BuildType;
import com.blazeloader.util.version.SaveableVersion;

/**
 * Basic implementation of a version. Supports saving and loading from a config file and can have only one component.
 */
public class SingleVersion extends SaveableVersion<SingleVersion> {
    public SingleVersion(String id, String name, BuildType buildType, int version) {
        super(id, name, buildType, version);
    }
    
    /**
     * Gets the first component.
     */
    public int getFirst() {
        return getNthComponent(0);
    }
    
    protected SingleVersion createVersion(String id, String name, BuildType buildType, int... parts) {
    	return new SingleVersion(id, name, buildType, parts[0]);
    }
}
