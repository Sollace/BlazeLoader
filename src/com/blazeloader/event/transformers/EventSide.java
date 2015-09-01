package com.blazeloader.event.transformers;

public enum EventSide {
    //XXX: Reminder to update these when changing package structure.
	CLIENT("com.blazeloader.event.handlers.client.EventHandlerClient"),
	SERVER("com.blazeloader.event.handlers.EventHandler"),
	INTERNAL("com.blazeloader.event.handlers.InternalEventHandler"),
	INTERNAL_CLIENT("com.blazeloader.event.handlers.client.InternalEventHandlerClient"),
	/**Handler for events injected into EntityVillager*/
	VILLAGER("com.blazeloader.event.handlers.VillagerEventHandler");
	
	private final String handler;
	
	EventSide(String h) {
		handler = h;
	}
	
	public String getHandler() {
		return handler;
	}
}