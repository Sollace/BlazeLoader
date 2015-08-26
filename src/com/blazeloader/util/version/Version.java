package com.blazeloader.util.version;

import java.util.function.Function;

/**
 * Interface for anything with a version.
 * 
 * @param <T> This version to be returned from/given to factory functions.
 */
public interface Version<T extends Version<T>> {
    /**
     * The number of version categories this version has.  1 for major only; 2 for major and minor; 3 for major, minor, and patch; 4 for major, minor, patch, and additional.
     *
     * @return The number of version categories this version has.
     */
    public int getVersionDepth();

    /**
     * Gets the version of the specified depth.
     *
     * @param num The depth of the version, must be less than getVersionDepth()
     * @return Return the version value for this depth.
     */
    public int getNthComponent(int num);

    /**
     * Gets the ID used internally to identify this version.
     *
     * @return The ID used internally to identify this version
     */
    public String getID();

    /**
     * Gets the user-friendly name of this version.
     *
     * @return The user-friendly name of this version.
     */
    public String getName();

    /**
     * Gets this version as a String, for example "beta 1.2", "alpha 1.2.3", or "1.2.3_4"
     *
     * @return Gets this version as a String.
     */
    public String getVersionString();
    
    /**
     * Gets this version's numbers as a String, for example "1.2", "1:2:3", or "1.2.3_4"
     *
     * @return Gets this version as a String.
     */
    public String getComponentsString();

    /**
     * Gets the build type of this version.
     *
     * @return The build type of this version.
     */
    public BuildType getBuildType();
    
    /**
     * Sets a function used to convert this version to a string for getVersionString.
     */
    public T setFormat(Function<VersionNumber<T>, String> func);
    
    /**
     * Sets a function used to convert this version's numbers to a string for getComponentsString.
     */
    public T setComponentFormat(Function<VersionNumber<T>, String> func);
}
