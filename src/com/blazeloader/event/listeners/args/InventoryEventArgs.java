package com.blazeloader.event.listeners.args;

import net.minecraft.item.ItemStack;

/**
 * Event arguments for inventory events.
 */
public class InventoryEventArgs extends Cancellable {
	
	private ItemStack theItem;
	private boolean stackChanged = false;
	
	/**
	 * Returns true if the itemstack for this event has been changed.
	 */
	public boolean isDirty() {
		return stackChanged;
	}
	
    public InventoryEventArgs(ItemStack stack) {
    	theItem = stack;
    }
    
    /**
     * Sets the itemstack associated with this event.
     * @param stack	The stack to use.
     */
    public void setItemStack(ItemStack stack) {
    	theItem = stack;
    	stackChanged = true;
    }
    
    /**
     * Gets the itemstack associated with this event.
     * @return
     */
    public ItemStack getItemStack() {
    	return theItem;
    }
}