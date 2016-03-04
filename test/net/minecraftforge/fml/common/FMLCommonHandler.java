package net.minecraftforge.fml.common;

import com.blazeloader.bl.main.BLMain;

/**
 * Dummy class is dummy class. :D
 */
public class FMLCommonHandler {
	
	public static final FMLCommonHandler instance() {
		BLMain.LOGGER_FULL.logInfo("[FMLCommonHandler] A wild INSTANCE has appeared!");
		return new FMLCommonHandler();
	}
	
	public void exitJava(int exitCode, boolean hardExit) {
		BLMain.LOGGER_FULL.logInfo("[FMLCommonHandler] Exit Code:" + exitCode);
		BLMain.LOGGER_FULL.logInfo("[FMLCommonHandler] IsHard:" + hardExit);
		BLMain.LOGGER_FULL.logInfo("[FMLCommonHandler] Exit Java?");
		BLMain.LOGGER_FULL.logInfo("[FMLCommonHandler] Nah, this is just a test.");
	}
}
