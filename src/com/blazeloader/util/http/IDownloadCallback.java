package com.blazeloader.util.http;

import java.io.InputStream;

/**
 * Callback object for downloads.
 * 
 */
public interface IDownloadCallback {
	/**
	 * Called when a response is received from the server.
	 * 
	 * @param stream	Raw stream of data being delivered.
	 */
	public void success(InputStream stream);
	
	/**
	 * Called when a negative response is received.
	 */
	public default void error(int responseCode) {
		
	}
}