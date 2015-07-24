package com.blazeloader.util;

public interface Result {
	
	public static Nothing Nothing() {
		return new Nothing();
	}
	
	public static <T> Something<T> Something(T result) {
		return new Something(result);
	}
	
	boolean isSomething();
	
	public final class Nothing implements Result {
		protected Nothing() {
			
		}
		
		public boolean isSomething() {
			return false;
		}
	}
	
	public final class Something<T> implements Result {
		private T result;
		
		protected Something(T object) {
			if (object == null) throw new IllegalArgumentException("value cannot be null");
			result = object;
		}
		
		public T getResult() {
			return result;
		}
		
		public boolean isSomething() {
			return true;
		}
	}
}
