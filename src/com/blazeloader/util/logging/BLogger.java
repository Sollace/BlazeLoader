package com.blazeloader.util.logging;

import com.blazeloader.bl.main.Settings;

import net.acomputerdog.core.logger.CLogger;
import net.acomputerdog.core.logger.LogLevel;

public class BLogger extends CLogger {
    private boolean logToFile = true;
    
    BLogger(String name, boolean includeDate, boolean includeTime) {
        super(name, includeDate, includeTime, LogLevel.DEBUG);
    }
    
    BLogger(String name, boolean includeDate, boolean includeTime, LogLevel minimumLevel) {
        super(name, includeDate, includeTime, minimumLevel);
    }
    
    protected String formatMessage(LogLevel level, String message) {
    	String result = super.formatMessage(level, message);
    	if (logToFile && Settings.logToFile) {
            LogManager.addLogEntry(this, result + "\r\n");
        }
    	return result;
    }
    
    public boolean canLogToFile() {
        return logToFile;
    }
    
    public void setLogToFile(boolean logToFile) {
        this.logToFile = logToFile;
    }
}
