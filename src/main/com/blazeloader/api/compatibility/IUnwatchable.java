package com.blazeloader.api.compatibility;

/**
 * Subscription interface for an object that accepts listeners for when it's value may change.
 * Also allows listeners to remove themselves.
 * 
 * @param <T>	The type of value this IWatchable contains.
 */
public interface IUnwatchable<T> extends IWatchable<T> {
	/**
	 * Unsubscribes from this item.
	 * @param subscription	Handler previously registered to the item
	 */
	public IUnwatchable<T> unwatch(ISubscription<T> subscription);
}
