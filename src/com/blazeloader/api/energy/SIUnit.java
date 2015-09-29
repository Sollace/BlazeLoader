package com.blazeloader.api.energy;

/**
 * Interface for a unit of energy.
 * 
 */
public interface SIUnit {
	
	/**
	* Gets the symbol used to represent this unit
	**/
	public String getSymbol();
	
	/**
	* Converts a value in the base units to this unit
	**/
	public float convertTo(float value);
	
	/**
	* Converts a value in this unit to the base units.
	**/
	public float getUniversalValue(float value);
}