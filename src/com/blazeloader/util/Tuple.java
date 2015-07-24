package com.blazeloader.util;

/**
 * Generic version of {@code net.minecraft.util.Tuple}
 * Wraps two objects.
 */
public class Tuple<T, V> {
	private T one;
	private V two;
	
	public Tuple(T objectOne, V objectTwo) {
		one = objectOne;
		two = objectTwo;
	}
	
	public T getFirst() {
		return one;
	}
	
	public V getSecond() {
		return two;
	}
}
