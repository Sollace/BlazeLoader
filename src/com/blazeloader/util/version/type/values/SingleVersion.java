package com.blazeloader.util.version.type.values;

import com.blazeloader.util.version.BuildType;
import com.blazeloader.util.version.type.BasicVersion;

public class SingleVersion extends BasicVersion {
    public SingleVersion(String id, String name, BuildType buildType, int version) {
        super(id, name, buildType, version);
    }
    
    public int getVersion1() {
        return getVersionNum(0);
    }
}
