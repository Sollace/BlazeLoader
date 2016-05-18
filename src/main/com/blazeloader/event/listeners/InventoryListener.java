package com.blazeloader.event.listeners;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.event.listeners.args.InventoryEventArgs;

/**
 * Interface for mods that handle inventory events
 * 
 */
public interface InventoryListener extends BLMod {
	
	/**
	 * Called when a player presses the Q key.
	 * <p>
	 * Occurs before onDropItem
	 * 
	 * @param player		The player
	 * @param dropAll		True if an entire stack is being dropped
	 * @param args			Inventory arguments, contains the item for this event
	 */
	public void onDropOneItem(EntityPlayer player, boolean dropAll, InventoryEventArgs args);
	
	/**
	 * Called when an entity tries to drop an item.
	 * 
	 * @param droppingEntity	The entity trying to drop an item
	 * @param itemDropped		The itemstack being dropped
	 * @param args			Inventory arguments, contains the item for this event
	 */
	public void onDropItem(Entity droppingEntity, boolean dropAround, boolean traceItems, InventoryEventArgs args);
	
	/**
	 * Called after an entity picks up an item.
	 * <p>
	 * If you wish to change the default pickup mechanic you will have to use PlayerListener.onEntityCollideWithPlayer to receive the event more early.
	 * 
	 * @param entity		The entity picking up the item
	 * @param itemEntity	The entity being picked up
	 * @param amount		The number of items being picked up from this entity
	 */
	public void onItemPickup(Entity entity, Entity itemEntity, int amount);
	
	/**
	 * Called when an entity is about to pickup an item to equip as armour
	 * 
	 * @param entity		The entity picking up the item
	 * @param itemEntity	The entity being picked up
	 * @param args			Inventory arguments, contains the item for this event
	 */
	public void onEntityEquipItem(Entity entity, Entity itemEntity, InventoryEventArgs args);
	
	/**
	 * Occurs when the player changes the selected slot in their hotbar
	 * @param player			The player
	 * @param item				The item placed in the current slot
	 * @param selectedSlot		The slot being selected.
	 * @return True to allow the slot to change, otherwise false to cancel the event.
	 */
	public boolean onSlotSelectionChanged(EntityPlayer player, ItemStack item, int selectedSlot);
}
