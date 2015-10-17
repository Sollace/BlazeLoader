package com.blazeloader.api.energy;

import java.text.NumberFormat;

public class CommonEnergyUnit implements SIUnit {
	/**
	 * The common SIUnit used as an intermediate between other types of energies.
	 * 
	 * Conversion is done in a relative fashion so it is up to modders to decide how their units compare to this.
	 */
	public static final SIUnit COMMON_SI_UNIT = new CommonEnergyUnit("C");
	
	/**
	 * The formatter used to represent values in this unit
	 */
	protected static final NumberFormat format;
	static {
		format = NumberFormat.getInstance();
		format.setGroupingUsed(true);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
	}
	
	private final String symbol;
	
	private CommonEnergyUnit(String symbol) {
		this.symbol = symbol;
	}
	
	@Override
	public String getSymbol() {
		return symbol;
	}
	
	@Override
	public String getDisplayName() {
		return getSymbol();
	}
	
	@Override
	public String formatValue(float value) {
		return getSymbol() + " " + format.format(value).replace(".", ":");
	}
	
	@Override
	public float convertTo(float value) {
		return value;
	}
	
	@Override
	public float getUniversalValue(float value) {
		return value;
	}
	
	@Override
	public boolean isElectrical() {
		return false;
	}

	@Override
	public boolean isNewtonForce() {
		return false;
	}

	@Override
	public boolean isHydraulic() {
		return false;
	}

	@Override
	public boolean isPressure() {
		return false;
	}

	@Override
	public boolean isThermal() {
		return false;
	}

	@Override
	public boolean isLuminous() {
		return false;
	}
}