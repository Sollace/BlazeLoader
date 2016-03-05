package com.blazeloader.api.network;

/**
 * Enum to specify handling for a specific side.
 */
public enum Side {
	/**
	 * Handled by both client and server
	 */
	BOTH,
	/**
	 * Handled by only the server (Dedicated or Internal)
	 */
	SERVER,
	/**
	 * Handled only by the client.
	 */
	CLIENT
}
