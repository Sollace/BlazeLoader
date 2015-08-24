package com.blazeloader.util.version;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.blazeloader.util.JSArrayUtils;
import com.google.common.collect.Lists;

/**
 * Class that implements basic functionality of IVersioned. Automatically adds the version to Version.class
 */
public abstract class MultiPartVersion extends AbstractVersion {
	
	private final int versionDepth;
    private final int[] versionParts;
    private final String versionString;

    public MultiPartVersion(String id, String name, BuildType buildType, int... versionParts) {
        super(id.replace(".", "-"), name, buildType);
        if (versionParts == null || versionParts.length == 0) {
            throw new IllegalArgumentException("versionParts cannot be null and must have at least one element!");
        }
        versionDepth = versionParts.length;
        boolean started = false;
        List<Integer> trimmedVersionParts = new ArrayList<Integer>();
        for (int i = versionParts.length - 1; i >= 0; i--) {
        	if (versionParts[i] < 0) {
        		versionParts[i] = 0;
        	}
        	if (versionParts[i] != 0) started = true;
        	if (started) {
        		trimmedVersionParts.add(versionParts[i]);
        	}
        }
        
        this.versionParts = ArrayUtils.toPrimitive(JSArrayUtils.toArray(Lists.reverse(trimmedVersionParts), Integer.class));
        this.versionString = createVersionString(id.replace(".", "-"), this.versionParts);
    }

    /**
     * The number of version categories this version has.  1 for major only; 2 for major and minor; 3 for major, minor, and patch; 4 for major, minor, patch, and additional.
     *
     * @return The number of version categories this version has.
     */
    @Override
    public int getVersionDepth() {
        return versionDepth;
    }

    /**
     * Gets the version of the specified depth.
     *
     * @param num The depth of the version, must be less than getVersionDepth()
     * @return Return the version value for this depth.
     */
    @Override
    public int getNthComponent(int num) {
        if (num < getVersionDepth()) {
        	if (num < 0) num = 0;
        	if (num > versionParts.length) return 0;
            return versionParts[num];
        }
        throw new IllegalArgumentException("num must be less than getVersionDepth!");
    }
    
    @Override
    public String getVersionString() {
        return (getBuildType().humanReadable() + " " + versionString).trim();
    }
    
    @Override
    public String getComponentsString() {
    	return versionString;
    }
    
    private static String createVersionString(String id, int... versionParts) {
        StringBuilder builder = new StringBuilder();
        if (versionParts != null && versionParts.length > 0) {
            for (int index = 0; index < versionParts.length; index++) {
                builder.append(versionParts[index]);
                if (index < versionParts.length - 1) {
                    builder.append(".");
                }
            }
        } else {
            builder.append("null");
        }
        return builder.toString();
    }
}
