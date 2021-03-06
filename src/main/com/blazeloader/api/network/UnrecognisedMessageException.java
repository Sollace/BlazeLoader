package com.blazeloader.api.network;

/**
 * Exception to indicate an invalid message type
 */
public class UnrecognisedMessageException extends IllegalArgumentException {
	public UnrecognisedMessageException(String channelName, IMessage message, String side) {
		super("Messages of type " + message.getClass() + " are not registered on channel " + channelName + " for side " + side);
	}
}
