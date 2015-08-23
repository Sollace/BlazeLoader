package com.blazeloader.util.version.type.values;

import com.blazeloader.util.version.BuildType;
import com.blazeloader.util.version.type.BasicVersion;

public class TripleVersion extends BasicVersion {
	
    public TripleVersion(String id, String name, BuildType buildType, int version1, int version2, int version3) {
        super(id, name, buildType, version1, version2, version3);
    }
    
    public int getVersion1() {
        return getVersionNum(0);
    }
    
    public int getVersion2() {
        return getVersionNum(1);
    }
    
    public int getVersion3() {
        return getVersionNum(2);
    }
}
