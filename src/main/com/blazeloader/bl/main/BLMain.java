package com.blazeloader.bl.main;

import java.util.Collections;
import java.util.List;

import org.spongepowered.asm.mixin.MixinEnvironment.CompatibilityLevel;

import net.acomputerdog.core.logger.Logger;
import net.acomputerdog.core.logger.CLogger;
import net.acomputerdog.core.logger.LogLevel;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;

import com.blazeloader.api.ApiServer;
import com.blazeloader.bl.interop.ForgeModloader;
import com.mumfrey.liteloader.api.CoreProvider;
import com.mumfrey.liteloader.api.CustomisationProvider;
import com.mumfrey.liteloader.api.EnumeratorModule;
import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.api.MixinConfigProvider;
import com.mumfrey.liteloader.api.Observer;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;

/**
 * BL main class
 *
 * Has odd structure like "getClient" because while this will always be running on a server instance, it may not be running on a client.
 */
public class BLMain implements MixinConfigProvider {
    private static BLMain instance;

    /**
     * Logger that logs date and time
     */
    public static final Logger LOGGER_FULL = new CLogger("BlazeLoader", true, true, LogLevel.DEBUG);
    /**
     * Logger that logs time but not date
     */
    public static final Logger LOGGER_MAIN = new CLogger("BlazeLoader", false, true, LogLevel.DEBUG);
    /**
     * Logger that does not log date or time
     */
    public static final Logger LOGGER_FAST = new CLogger("BlazeLoader", false, false, LogLevel.DEBUG);
    
    private static boolean isClient;
    
    /**
     * the partial render tick for the client
     */
    protected static float partialTicks = 0;
    
    /**
     * number of ticks that the game has been running
     */
    protected static int numTicks = 0;

    public final LoaderEnvironment environment;
    public final LoaderProperties properties;

    private CommandHandler commandHandler;
    
    public static BLMain instance() {
        return instance;
    }
    
    public static boolean isClient() {
    	return isClient;
    }
    
    public static float getPartialTicks() {
    	return partialTicks;
    }
    
    public static int getTicks() {
    	return numTicks;
    }
    
    BLMain(LoaderEnvironment environment, LoaderProperties properties) {
        if (instance != null) {
            throw new IllegalStateException("BLMain cannot be created twice!");
        }
        instance = this;
        this.environment = environment;
        this.properties = properties;

        LOGGER_FULL.setMinimumLogLevel(LogLevel.valueOf(Settings.minimumLogLevel));
        LOGGER_MAIN.setMinimumLogLevel(LogLevel.valueOf(Settings.minimumLogLevel));
        LOGGER_FAST.setMinimumLogLevel(LogLevel.valueOf(Settings.minimumLogLevel));

        BLMain.LOGGER_FULL.logInfo("BlazeLoader initialized.");
        init();
    }

    public String[] getRequiredTransformers() {
    	return new String[]{ "com.blazeloader.util.transformers.ONFTransformer" };
    }

    public String[] getRequiredDownstreamTransformers() {
        return null;
    }

    public List<EnumeratorModule> getEnumeratorModules() {
        return null;
    }

    public List<CoreProvider> getCoreProviders() {
        return Collections.singletonList((CoreProvider)BlazeLoaderCoreProvider.instance());
    }
    
    public List<InterfaceProvider> getInterfaceProviders() {
    	return Collections.singletonList(new BlazeLoaderInterfaceProvider());
    }

    public List<Observer> getObservers() {
        return null;
    }
    
    public List<CustomisationProvider> getCustomisationProviders() {
        return Collections.singletonList(BlazeLoaderBrandingProvider.instance());
    }

    public List<Observer> getPreInitObservers() {
        return null;
    }

    public final void shutdown(String message, int code) {
        try {
            LOGGER_FULL.logFatal("Unexpected shutdown detected!");
            LOGGER_FULL.logFatal("Message: " + message);
            if (!initiateShutdown()) {
                LOGGER_FULL.logFatal("Game is not running, closing immediately with code " + code + "!");
                ForgeModloader.exitJVM(code);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            ForgeModloader.exitJVM(code);
        }
    }
    
    protected boolean initiateShutdown() {
    	MinecraftServer server = MinecraftServer.getServer();
    	if (server == null) return false;
    	LOGGER_FULL.logFatal("Shutting down server...");
    	server.initiateShutdown();
    	return true;
    }
    
    public void tick(boolean clock, float partialTicks, boolean isInGame) {
    	MinecraftServer server = ApiServer.getServer();
    	if (server != null) {
    		numTicks = server.getTickCounter();
    	} else {
    		numTicks = 0;
    	}
    }
    
    public void init() {
        isClient = supportsClient();
    }
    
    public boolean supportsClient() {
        return false;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler == null ? commandHandler = createCommandHandler() : commandHandler;
    }

    protected CommandHandler createCommandHandler() {
    	return new ServerCommandManager();
    }

    public String getPluginChannelName() {
        return "BLAZELOADER";
    }

	@Override
	public CompatibilityLevel getCompatibilityLevel() {
		return CompatibilityLevel.JAVA_8;
	}

	@Override
	public String[] getMixinConfigs() {
		return new String[] { "mixins.blazeloader.json" };
	}

	@Override
	public String[] getErrorHandlers() {
		return null;
	}
}
