package com.blazeloader.util.version;

import com.blazeloader.util.config.IStringable;

public abstract class SaveableVersion<T extends SaveableVersion<T>> extends MultiPartVersion implements IStringable<T> {
	public SaveableVersion(String id, String name, BuildType buildType, int... versionParts) {
		super(id, name, buildType, versionParts);
	}
	
    @Override
	public T fromString(String string) {
		string = string.trim();
		if (string.startsWith("{")) string = string.substring(1);
		if (string.endsWith("}")) string = string.substring(0, string.length() - 1);
		string = string.trim();
		String[] parts = string.split(", ");
		if (parts.length == 3) {
			String[] versionString = parts[0].split(".");
			int[] versionParts = new int[versionString.length - 1];
			for (int i = 1; i < versionString.length; i++) {
				try {
					versionParts[i - 1] = Integer.valueOf(versionString[i]);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Invalid string", e);
				}
			}
			BuildType buildType = BuildType.STABLE;
			try {
				buildType = BuildType.valueOf(parts[2].toUpperCase());
			} catch (Throwable e) {
				throw new IllegalArgumentException("Invalid string", e);
			}
			return createVersion(versionString[0], parts[1], buildType, versionParts);
		}
		return null;
	}
    
    protected abstract T createVersion(String id, String name, BuildType buildType, int... parts);
}
