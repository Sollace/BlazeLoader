package com.blazeloader.api.energy;

class CommonEnergyUnit implements SIUnit {
	/**
	 * The common SIUnit used as an intermediate between other types of energies.
	 * 
	 * Conversion is done in a relative fashion so it is up to modders to decide how their units compare to this.
	 */
	public static final SIUnit COMMON_SI_UNIT = new CommonEnergyUnit("C");
	
	private final String symbol;
	
	private CommonEnergyUnit(String symbol) {
		this.symbol = symbol;
	}
	
	@Override
	public String getSymbol() {
		return symbol;
	}
	
	@Override
	public float convertTo(float value) {
		return value;
	}
	
	@Override
	public float getUniversalValue(float value) {
		return value;
	}
}