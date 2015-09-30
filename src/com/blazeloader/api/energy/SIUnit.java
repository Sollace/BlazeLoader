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
	 * Gets the human friendly name of this unit for display.
	 */
	public String getDisplayName();
	
	/**
	* Converts a value in the base units to this unit
	**/
	public float convertTo(float value);
	
	/**
	* Converts a value in this unit to the base units.
	**/
	public float getUniversalValue(float value);
	
	/**
	 * Return true if this unit is associated with electrical energy.
	 * i.e Current (I), Voltage (V), Amperes (A) 
	 */
	public boolean isElectrical();
	
	/**
	 * Return true if this unit deals with a physical force.
	 * i.e Newtons (N)
	 */
	public boolean isNewtonForce();
	
	/**
	 * Return true if this unit is associated forces creatued due to a fluid.
	 * i.e Hyrdaulic Pressure.
	 */
	public boolean isHydraulic();
	
	/**
	 * A more generic version of isHydraulic.
	 * Return true if this unit is any form of pressure.
	 * i.e Pascals (Pa)
	 */
	public boolean isPressure();
	
	/**
	 * Return true if this unit is used to measure heat, or temperature.
	 * i.e Degrees Celsius (C), Faranheight (F), etc.
	 */
	public boolean isThermal();
	
	/**
	 * Return true if this unit deals with brightness.
	 */
	public boolean isLuminous();
}