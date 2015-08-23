package com.blazeloader.util.version.type;

import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.util.version.MultiPartVersion;
import com.blazeloader.util.version.BuildType;

/**
 * Describes the version of a BLMod. ID and Name are obtained from the mod.
 * <p>
 * Cannot be saved to a config file.
 */
public class ModVersion extends MultiPartVersion {
    private final BLMod mod;

    public ModVersion(BuildType buildType, BLMod mod, int... versionParts) {
        super(mod.getModId(), mod.getName(), buildType, versionParts);
        this.mod = mod;
    }
    
    /**
     * Gets the numeric component of this ModVersion.
     */
    public String getVersionNum() {
        StringBuilder builder = new StringBuilder((getVersionDepth() * 2) - 1);
        for (int index = 0; index < getVersionDepth(); index++) {
            if (index != 0) {
                builder.append('.');
            }
            builder.append(getNthComponent(index));
        }
        return builder.toString();
    }
    
    /**
     * Gets the mod instance associated with this version.
     */
    public BLMod getMod() {
        return mod;
    }
}
