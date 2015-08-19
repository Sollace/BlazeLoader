package com.blazeloader.event.listeners;

import com.mumfrey.liteloader.PluginChannelListener;
import com.mumfrey.liteloader.ServerPluginChannelListener;

/**
 * Iterface for mods who want to use plugin channels on both the client and server 
 */
public interface PacketChannelListener extends PluginChannelListener, ServerPluginChannelListener {

}
