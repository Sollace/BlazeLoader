package com.blazeloader.api.network;

import io.netty.buffer.ByteBuf;

/**
 * A message to be sent or recieved over a PacketChannel
 *
 */
public interface IMessage {
	
	/**
	 * Loads this message's variables from a byte buffer when received
	 */
	public void fromBytes(ByteBuf buf);
	
	/**
	 * Writes this message's variables to a byte buffer when sent
	 */
	public void toBytes(ByteBuf buf);
}