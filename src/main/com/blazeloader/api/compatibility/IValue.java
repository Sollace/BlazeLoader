package com.blazeloader.api.compatibility;

/**
 * A type-safe holder for some value.
 *
 * @param <T>	The type of value held.
 */
public interface IValue<T> {
	/**
	 * Gets the current value.
	 */
	public T get();
	
	/**
	 * Sets the current value to the given value.
	 */
	public void set(T val);
	
	/**
	 * Gets a string representation of the type this property takes.
	 * <br>The type is the name of the class for values accepted by this item.
     * @return The type of the item.
	 */
	public String getType();
}
