package com.blazeloader.api.compatibility;

/**
 * Subscription interface for an object that accepts listeners for when it's value may change.
 * 
 * @param <T>	The type of value this IWatchable contains.
 */
public interface IWatchable<T> extends IValue<T> {
    /**
     * Subscribes to this item to be notified when it's value changes.
     * @param subscriber	Handler to receive the event
     */
	public IWatchable<T> watch(ISubscription<T> subscriber);
}
