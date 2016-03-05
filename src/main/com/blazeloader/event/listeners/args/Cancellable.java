package com.blazeloader.event.listeners.args;

abstract class Cancellable {
	private boolean canceled = false;
	
	/**
	 * Returns true if the event must be discarded.
	 * </br>
	 * Other mods may still receive the event after {@code cancel} is called so you should always check this first when handling an event.
	 */
	public boolean isCancelled() {
		return canceled;
	}
	
	/**
	 * Cancels this event. The player will not take any fall damage or playe the landing sound.
	 */
	public void cancel() {
		canceled = true;
	}
}
