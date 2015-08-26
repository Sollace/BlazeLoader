package com.blazeloader.util.version;

/**
 * Represents the build type of a given version
 */
public enum BuildType {
    /**
     * A stable, release, well tested build
     */
    STABLE(""),
    /**
     * A mostly stable build intended for testing
     */
    RELEASE_CANDIDATE("RC"),
    /**
     * A patch build.  Based on stable code but not highly tested.
     */
    PATCH(""),
    /**
     * A beta release.  Usable but only for experienced users.  Expect some issues.
     */
    BETA("Beta"),
    /**
     * An alpha release.  Usable but only for experienced users.  Expect many issues.
     */
    ALPHA("Alpha"),
    /**
     * A preview release.  Should only be used to see what has changed between versions.
     */
    PREVIEW("Pre"),
    /**
     * A build straight from the newest code.  Completely in development, may not even compile or function.
     */
    DEVELOPMENT("Dev");
	
	private final String simpleName;
	
	BuildType(String readable) {
		simpleName = readable;
	}
	
	public String humanReadable() {
		return simpleName;
	}
}
