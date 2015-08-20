package com.blazeloader.api.client;

import com.blazeloader.api.ApiServer;
import com.blazeloader.bl.main.BlazeLoaderCoreProvider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.WorldSettings;

public class ApiClient {
	
	/**
	 * Enabled or disables client profiling
	 * 
	 * @param enabled
	 */
	public static void setProfiling(boolean enabled) {
		Minecraft client = getClient();
		client.gameSettings.showDebugInfo = enabled;
		client.gameSettings.showDebugProfilerChart = enabled;
		client.gameSettings.hideGUI = enabled;
		client.mcProfiler.profilingEnabled = enabled;
	}
	
    /**
     * Client version of ApiPlayer.getPlayer. Will always return Minecraft.thePlayer
     *
     * @return the client's player
     */
    public static EntityPlayerSP getPlayer() {
        return getClient().thePlayer;
    }
    
    public static GameSettings getGameSettings() {
    	return getClient().gameSettings;
    }
    
    /**
     * Gets the render item currently used
     */
    public static RenderItem getRenderItem() {
    	return getClient().renderItem;
    }
    
    /**
     * Gets the resource manager
     */
    public static IReloadableResourceManager getResourceManager() {
    	return (IReloadableResourceManager)getClient().getResourceManager();
    }
    
    /**
     * Gets the texture manager
     */
    public static TextureManager getTextureManager() {
    	return getClient().getTextureManager();
    }
    
    /**
     * Gets the item renderer
     */
    public static ItemRenderer getItemRenderer() {
    	return getClient().getItemRenderer();
    }
    
    /**
     * Gets the render manager currently used.
     * @return
     */
    public static RenderManager getRenderManager() {
    	return getClient().getRenderManager();
    }
    
    public static EffectRenderer getEffectRenderer() {
    	return getClient().effectRenderer;
    }
    
    /**
     * The current instance of the Minecraft client
     */
    public static Minecraft getClient() {
        return (Minecraft) BlazeLoaderCoreProvider.instance().getGameEngine().getClient();
    }
    
	/**
	 * Opens server to lan.
	 * <p>
	 * Only has an effect on the Integrated Server
	 * 
	 * @param type			The gametype
	 * @param allowCheats	True to let players execute commands (cheats)
	 */
	public static void shareToLAN(WorldSettings.GameType type, boolean allowCheats) {
		IntegratedServer server = getIntegratedServer();
		if (server != null) {
			server.shareToLAN(type, allowCheats);
		}
	}
    
    /**
     * The current IntegratedServer instance
     */
    public static IntegratedServer getIntegratedServer() {
        return (IntegratedServer)ApiServer.getServer();
    }
}
