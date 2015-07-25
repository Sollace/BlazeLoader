package com.blazeloader.test;

import java.io.File;

import org.apache.commons.lang3.NotImplementedException;

import com.mumfrey.liteloader.api.manager.APIAdapter;
import com.mumfrey.liteloader.api.manager.APIProvider;
import com.mumfrey.liteloader.core.EnabledModsList;
import com.mumfrey.liteloader.core.LiteLoaderVersion;
import com.mumfrey.liteloader.interfaces.LoaderEnumerator;
import com.mumfrey.liteloader.launch.ClassTransformerManager;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;

public class DebugEnvironment implements LoaderEnvironment, LoaderProperties {
	
	private EnvironmentType type;
	
	public DebugEnvironment(EnvironmentType type) {
		this.type = type;
	}
	
	public File getGameDirectory() {throw new NotImplementedException("Debug");}

	public File getAssetsDirectory() {throw new NotImplementedException("Debug");}

	public String getProfile() {return "Debug";}

	public File getModsFolder() {throw new NotImplementedException("Debug");}
	
	public EnvironmentType getType() {return type;}

	public APIAdapter getAPIAdapter() {throw new NotImplementedException("Debug");}

	public APIProvider getAPIProvider() {throw new NotImplementedException("Debug");}

	public EnabledModsList getEnabledModsList() {throw new NotImplementedException("Debug");}

	public LoaderEnumerator getEnumerator() {throw new NotImplementedException("Debug");}

	public File getVersionedModsFolder() {throw new NotImplementedException("Debug");}

	public File getConfigBaseFolder() {throw new NotImplementedException("Debug");}

	public File getCommonConfigFolder() {throw new NotImplementedException("Debug");}

	public File getVersionedConfigFolder() {throw new NotImplementedException("Debug");}

	public File inflectVersionedConfigPath(LiteLoaderVersion version) {throw new NotImplementedException("Debug");}
	
	public boolean addCascadedTweaker(String tweakClass, int priority) {return false;}

	public ClassTransformerManager getTransformerManager() {throw new NotImplementedException("Debug");}

	public boolean loadTweaksEnabled() {return false;}

	public String getBranding() {return "Debug";}

	public void setBooleanProperty(String propertyName, boolean value) {}

	public boolean getBooleanProperty(String propertyName) {return false;}

	public boolean getAndStoreBooleanProperty(String propertyName, boolean defaultValue) {return defaultValue;}

	public void setIntegerProperty(String propertyName, int value) {}

	public int getIntegerProperty(String propertyName) {return 0;}

	public int getAndStoreIntegerProperty(String propertyName, int defaultValue) {return defaultValue;}

	public int getLastKnownModRevision(String modKey) {return 0;}

	public void storeLastKnownModRevision(String modKey) {}

	public void writeProperties() {}

}
