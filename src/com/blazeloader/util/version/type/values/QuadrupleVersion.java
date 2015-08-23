package com.blazeloader.util.version.type.values;

import com.blazeloader.util.version.BuildType;
import com.blazeloader.util.version.type.BasicVersion;

public class QuadrupleVersion extends BasicVersion {
	
    public QuadrupleVersion(String id, String name, BuildType buildType, int version1, int version2, int version3, int version4) {
        super(id, name, buildType, version1, version2, version3, version4);
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
    
    public int getVersion4() {
        return getVersionNum(3);
    }
}
