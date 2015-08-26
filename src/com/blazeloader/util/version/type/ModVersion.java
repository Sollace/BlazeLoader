package com.blazeloader.util.version.type;

import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.util.version.AbstractVersion;
import com.blazeloader.util.version.BuildType;

/**
 * Describes the version of a BLMod. ID and Name are obtained from the mod.
 * <p>
 * Cannot be saved to a config file.
 */
public class ModVersion extends AbstractVersion<ModVersion> {
    private final BLMod theMod;
    
    public ModVersion(BuildType buildType, BLMod mod, int... versionParts) {
        super(mod.getModId(), mod.getName(), buildType, versionParts);
        theMod = mod;
    }
    
    /**
     * Gets the mod instance associated with this version.
     */
    public BLMod getMod() {
        return theMod;
    }
    
    /**
     * Returns Basic version with the same attributes as this version.
     * The new copy will not be registered into Versions, rather the original will remain and be returned from Versions.getVersionOf.
     * <p>
     * The BasicVersion may then be saved to a config file.
     */
    public BasicVersion copyOf() {
    	return new BasicVersion(this);
    }
}
