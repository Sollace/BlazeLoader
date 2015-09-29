package com.blazeloader.api.energy;

/**
 * A node serves as the junction between components. Energy can be transferred into/out of this node.
 * 
 * @param <T>	The units of energy accepted by this node.
 */
public interface Node<T extends SIUnit> {
	
	/**
	 * Checks if this node can transfer energy in the specified direction and unit.
	 **/
	public boolean accepts(EnumDirection direction, SIUnit unit);
	
	/**
	 * Gets the unit of energy used by this Node. All amounts passed to onRecieved and onTaken must be converted to use this unit.
	 **/
	public T getAcceptedUnit();
	
	/**
	 * @param amount energy transferred given in this node's accepted units
	 *
	 * @return true if the energy is accepted, i.e this node is not full, otherwise false
	 **/
	public boolean onRecieved(float amount);
	
	/**
	 * @param amount energy transferred given in this node's accepted units
	 *
	 * @return true if the energy may be taken, i.e this node contains enough energy, otherwise false
	 **/
	public boolean onTaken(float amount);
}