package com.blazeloader.event.listeners.args;

import com.mojang.authlib.GameProfile;

/**
 * Event arguments for a login attempt.
 */
public class LoginEventArgs {
	private GameProfile gameProfile;
	private String error = null;
	
	private boolean blocked = false;
	
	public LoginEventArgs(GameProfile profile) {
		gameProfile = profile;
	}
	
	/**
	 * The player has been rejected by an earlier mod.
	 */
	public boolean isBlocked() {
		return blocked;
	}
	
	/**
	 * Rejects this player.
	 * <p>
	 * Once the event has left the current handler there is no way to undo this.
	 * No other mods will recieve this event after that point.
	 * 
	 * @param errorMessage	The error message to be displayed on the client
	 */
	public void block(String errorMessage) {
		error = errorMessage;
		blocked = true;
	}
	
	/**
	 * Resets block states.
	 * <p>
	 * This serves as a fail safe for mods.
	 */
	public void unblock() {
		blocked = false;
		error = null;
	}
	
	/**
	 * The game profile for the player currently trying to login. 
	 */
	public GameProfile getGameProfile() {
		return gameProfile;
	}
	
	/**
	 * Gets the message currently set to be sent to the client.
	 */
	public String getMessage() {
		return error;
	}
}