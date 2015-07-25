package com.blazeloader.util.data;

/**
 * Used to indicate a return value or the lack thereof.
 * <p>
 * This pattern is based on Functional Programming designs where every function is required to take one value and return another.
 * There is no null value so instead they use Some(x) where x is some value, or None to indicate no value.
 */
public interface Option<T> {
	/**
	 * Indicates there is no result.
	 */
	public static None None() {
		return None.instance;
	}
	
	/**
	 * There was a result and it can be included in the return value.
	 */
	public static <T> Some<T> Some(T result) {
		return new Some(result);
	}
	
	/**
	 * Checks if this Result has a result value.
	 */
	public boolean isSomething();
	
	/**
	 * Gets the result value.
	 */
	public T getResult();
	
	public final class None<T> implements Option<T> {
		private static final None instance = new None();
		
		private None() {
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
		
		public String toString() {
			return "None";
		}
	}
	
	public final class Some<T> implements Option {
		private T result;
		
		private Some(T object) {
			if (object == null) throw new IllegalArgumentException("value cannot be null");
			result = object;
		}
		
		private Some() {
			throw new RuntimeException("Access Denied");
		}
		
		public T getResult() {
			return result;
		}
		
		public boolean isSomething() {
			return true;
		}
		
		public boolean equals(Object other) {
			return other instanceof Some && result.equals(((Some)other).result);
		}
		
		public String toString() {
			return "Some " + result.toString();
		}
	}
}
