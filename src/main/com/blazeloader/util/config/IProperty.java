package com.blazeloader.util.config;

import com.blazeloader.api.compatibility.IValue;

/**
 * Container object of a value-key pair in a config file.
 *
 * @param <T> the type of value it contains
 */
public interface IProperty<T> extends IValue<T> {
	/**
	 * Sets the default value
	 */
	public IProperty<T> setDefault(T newDef);
	
	/**
	 * Gets the default value
	 */
	public T getDefault();
	
	/**
	 * Sets the current value back to the default.
	 */
	public void reset();
	
	/**
	 * Sets a description/comment to be stored with this property.
	 */
	public IProperty<T> setDescription(String... desc);
	
	/**
	 * Gets the type this property takes as a class object.
	 */
	public Class<T> getTypeClass();
	
	/**
	 * The name of this property. Is also the key this property is registered under in config files and categories.
	 */
	public String getName();
	
	/**
	 * Returns an array of possible values if bounded, otherwise returns null.
	 * 
	 * This may be useful if you're working with enums.
	 */
	public T[] getPossibleValues();
}
