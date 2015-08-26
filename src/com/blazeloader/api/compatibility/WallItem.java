package com.blazeloader.api.compatibility;

/**
 * A shortcut reference to an item on the wall.
 * Allows for most functionality of the Wall applied directly to the underlying item.
 */
public class WallItem<T> {
	
	private final Wall.Entry<T> item;
	
	protected WallItem(Wall.Entry<T> item) {
		this.item = item;
	}
	
	/**
	 * Gets the value for this entry on the wall.
	 * 
	 * @return <I> value referenced by this item's key
	 */
	public T get() {
		return item.getValue();
	}
	
	/**
	 * Sets the value for this entry on the wall
	 * 
	 * @param <I> value		value to be stored under this item's key on the wall.
	 */
	public void set(T value) {
		item.setValue(value);
	}
	
    /**
     * Subscribes to this item to be notified when it changes.
     * @param subscriptionObject	Handler to receive the event
     */
	public void subScribeTo(ISubscription<T> subscriptionObject) {
		item.subscribe(subscriptionObject);
	}
	
	/**
	 * Unsubscribes from this item.
	 * @param subscriptionObject	Handler previously registered to the item
	 */
	public void unsubscribeFrom(ISubscription<T> subscriptionObject) {
		item.unsubscribe(subscriptionObject);
	}
	
    /**
     * Gets the type of this item.
     * <br>The type is the name of the class for values accepted by this item.
     * @return The type of the item.
     */
	public String getType() {
		return item.getType();
	}
}
