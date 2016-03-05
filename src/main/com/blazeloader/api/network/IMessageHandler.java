package com.blazeloader.api.network;

import net.minecraft.network.INetHandler;

/**
 * Handler for a message type recieved in a PacketChannel
 * @author Chris Albers
 *
 * @param <P>	The message received
 * @param <R>	The message returned as a response
 * @param <N>	The INetHandler received
 */
public interface IMessageHandler<P extends IMessage, R extends IMessage, N extends INetHandler> {
	
	/**
	 * Called when a message for this handler is received.
	 * 
	 * @param message	The message
	 * @param net		NetHandler this message was received from
	 * 
	 * @return	A message to send back as a response. Return null for no response.
	 */
	public R onMessage(P message, N net);
}