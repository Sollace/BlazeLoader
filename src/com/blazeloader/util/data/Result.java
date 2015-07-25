package com.blazeloader.util.data;

/**
 * Used to indicate a return value or the lack thereof.
 * <p>
 * This pattern is based on Functional Programming designs where every function is required to take one value and return another.
 * There is no null value so instead they use Result.Something or Result.Nothing to optionally return a value.
 * 
 */
public interface Result<T> {
	/**
	 * Indicates there is no result.
	 */
	public static Nothing Nothing() {
		return Nothing.instance;
	}
	
	/**
	 * There was a result and it can be included in the return value.
	 */
	public static <T> Something<T> Something(T result) {
		return new Something(result);
	}
	
	/**
	 * Checks if this Result has a result value.
	 */
	public boolean isSomething();
	
	/**
	 * Gets the result value.
	 */
	public T getResult();
	
	public final class Nothing<T> implements Result<T> {
		private static final Nothing instance = new Nothing();
		
		private Nothing() {
		}
		
		public boolean isSomething() {
			return false;
		}
		
		public T getResult() {
			throw new RuntimeException("Nothing has no result");
		}
		
		public final boolean equals(Object other) {
			return other == instance;
		}
	}
	
	public final class Something<T> implements Result {
		private T result;
		
		private Something(T object) {
			if (object == null) throw new IllegalArgumentException("value cannot be null");
			result = object;
		}
		
		private Something() {
			throw new RuntimeException("Access Denied");
		}
		
		public T getResult() {
			return result;
		}
		
		public boolean isSomething() {
			return true;
		}
		
		public boolean equals(Object other) {
			return other instanceof Something && result.equals(((Something)other).result);
		}
	}
}
