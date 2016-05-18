package com.blazeloader.api.client.tick;

import com.blazeloader.api.client.ApiClient;
import com.mumfrey.liteloader.client.overlays.IMinecraft;

/**
 * Client side tick functions
 *
 * WARNING:  These functions may not actually do what they seem
 *
 */
public class ApiTickClient {
    /**
     * Gets the game's tick rate.  Uses reflection only on first run.
     *
     * @return Returns the game's current tick rate.
     */
    public static float getTPS() {
        return ((IMinecraft)ApiClient.getClient()).getTimer().ticksPerSecond;
    }

    /**
     * Sets the game tick rate.
     *
     * @param tps The new tick rate.
     */
    public static void setTPS(float tps) {
    	((IMinecraft)ApiClient.getClient()).getTimer().ticksPerSecond = tps;
    }
}
