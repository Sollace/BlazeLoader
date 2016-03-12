package com.blazeloader.api.tick;

import com.blazeloader.bl.main.BLMain;

/**
 * Api for functions related to the world tick.
 */
public class ApiTick {
	
	/**
	 * Gets the partial ticks for client render.
	 * <p>
	 * Always returns false on a dedicated server.
	 * 
	 * @return float partial ticks
	 */
	public static float getPartialRenderTicks() {
		return BLMain.getPartialTicks();
	}
	
    /**
     * Gets the total number of ticks that the game has run for.
     *
     * @return long representing the number of ticks that the game has run for.
     */
    public static long getTotalTicksInGame() {
        return BLMain.getTicks();
    }
}
