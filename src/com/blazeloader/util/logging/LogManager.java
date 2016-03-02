package com.blazeloader.util.logging;

import com.blazeloader.bl.main.Settings;

import net.acomputerdog.core.java.Sleep;
import net.acomputerdog.core.logger.Logger;
import net.acomputerdog.core.logger.LogLevel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class LogManager {
    private static final Map<String, LoggerEntry> loggerCache = new HashMap<String, LoggerEntry>();
    
    private static final Queue<LogItem> logQueue = new ConcurrentLinkedQueue<LogItem>();
	
    private static final File logDir = new File("./logs/");
    private static final Logger loggerLogger = new BLogger("LogManager", false, true);
    
    private static final boolean logsInitialized;
    private static boolean loggerActive = true;
    
    public static Logger createLogger(String name) {
        return createLogger(name, false, true);
    }
    
    public static Logger createLogger(String name, boolean date, boolean time) {
        return createLogger(name, date, time, LogLevel.getByName(Settings.minimumLogLevel));
    }
    
    public static Logger createLogger(String name, boolean date, boolean time, LogLevel logLevel) {
        LoggerEntry logger = loggerCache.get(name);
        if (logger == null) {
            loggerCache.put(name, logger = new LoggerEntry(new BLogger(name, date, time, logLevel)));
        }
        return logger.logger;
    }
    
    static void addLogEntry(Logger logger, String message) {
        if (logsInitialized) logQueue.add(new LogItem(logger.getName(), message));
    }
    
    static {
        if (!(logDir.isDirectory() || logDir.mkdirs())) {
            loggerLogger.logError("Unable to create logging directory! Logs will not be recorded!");
            logsInitialized = false;
        } else {
            logsInitialized = true;
            new Thread() {
                public void start() {
                	Runtime.getRuntime().addShutdownHook(new Thread() {
                        public void run() {
                            loggerActive = false;
                            logInfo("Writing queued logs.");
                            if (logQueue != null) {
                            	LogItem log;
                                while ((log = logQueue.poll()) != null) log.write();
                            }
                            if (loggerCache != null && !loggerCache.isEmpty()) {
                                for (LoggerEntry entry : loggerCache.values()) entry.dispose();
                                loggerCache.clear();
                            }
                            logInfo("Done.");
                        }
                    });
                    setName("LogWriterThread");
                    setDaemon(true);
                    super.start();
                    logInfo("BLogger system initialized.");
                }
                
                public void run() {
                    logInfo("Log writer thread started.");
                    while (loggerActive) {
                    	if (logsInitialized && Settings.logToFile) {
                    		LogItem log;
	                        while ((log = logQueue.poll()) != null) log.write();
                    	}
                        Sleep.sleep(100);
                    }
                    logInfo("Log writer terminated.");
                }
                
                private boolean logInfo(String message) {
                    if (loggerLogger != null) return loggerLogger.logInfo(message);
                	System.out.println("[LogManager/Fallback][INFO] " + message);
                    return false;
                }
            }.start();
        }
    }
    
    private static final class LogItem {
        private final String logger;
        private final String message;

        private LogItem(String logger, String message) {
            this.logger = logger;
            this.message = message;
        }
        
        public void write() {
    		LoggerEntry entry = loggerCache.get(logger);
    		if (entry != null) entry.write(message);
        }
    }
    
    private static final class LoggerEntry {
    	private final Logger logger;
    	private boolean writerCreated = false;
    	private Writer writer;
    	
    	private LoggerEntry(Logger logger) {
    		this.logger = logger;
    	}
    	
    	private Writer getWriter() {
    		if (!writerCreated) {
                try {
                    writer = new FileWriter(new File(logDir, logger.getName() + ".bl_log.txt"));
                } catch (IOException e) {
                    loggerLogger.logWarning("Exception creating logger for \"" + logger.getName() + "\"!", e);
                }
                writerCreated = true;
            }
    		return writer;
    	}
    	
    	public void write(String message) {
    		try {
    			if (getWriter() != null) writer.write(message);
    		} catch (IOException e) {}
    	}
    	
    	public void dispose() {
    		if (writer != null) {
	    		try {
	                writer.flush();
	                writer.close();
	            } catch (Exception ignored) {}
    		}
    	}
    }
}
