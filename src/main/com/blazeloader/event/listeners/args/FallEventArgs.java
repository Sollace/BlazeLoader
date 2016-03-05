package com.blazeloader.event.listeners.args;

/**
 * Event arguments for falling entities
 */
public class FallEventArgs extends Cancellable {
	private float distance;
	private float multiplier;
	
	public FallEventArgs(float dist, float mult) {
		distance = dist;
		multiplier = mult;
	}
	
	/**
	 * Gets the total distance this player has fallen
	 */
	public float getFallDistance() {
		return distance;
	}
	
	/**
	 * Sets the total distance this player has fallen
	 */
	public void setFallDistance(float dist) {
		distance = dist;
	}
	
	/**
	 * Sets the amount total damage this player will recieve per block fallen.
	 */
	public void setDamageMultiplier(float mult) {
		multiplier = mult;
	}
	
	/**
	 * Gets the amount total damage this player will recieve per block fallen.
	 */
	public float getDamageMultiplier() {
		return multiplier;
	}
}