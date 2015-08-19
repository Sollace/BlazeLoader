package com.blazeloader.bl.main;

import java.io.File;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

import com.blazeloader.api.network.PacketChannel;
import com.blazeloader.api.network.Side;
import com.blazeloader.bl.network.BLPacketParticles;
import com.blazeloader.event.listeners.PacketChannelListener;
import com.blazeloader.util.reflect.Reflect;
import com.blazeloader.util.reflect.SimpleFunc;
import com.blazeloader.util.version.Versions;
import com.google.common.collect.ImmutableList;
import com.mumfrey.liteloader.core.CommonPluginChannelListener;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.PluginChannels;

public class BLPacketChannels extends PacketChannel implements PacketChannelListener {
	private static BLPacketChannels instance;
	
	public BLPacketChannels() {
		super(BLMain.instance().getPluginChannelName());
		registerMessageHandler(Side.CLIENT, new BLPacketParticles(), BLPacketParticles.Message.class, 0);
		instance = this;
	}
	
	public static BLPacketChannels instance() {
		return instance;
	}
	
	@Override
	public String getName() {
		return BLMain.instance().getPluginChannelName();
	}

	@Override
	public List<String> getChannels() {
		return ImmutableList.of(getName());
	}
	
	@Override
	public String getVersion() {
		return null;
	}

	@Override
	public void init(File configPath) {}

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

	@Override
	public void onCustomPayload(EntityPlayerMP sender, String channel, PacketBuffer data) {
		onPacketRecievedServer(channel, sender, data);
	}

	@Override
	public void onCustomPayload(String channel, PacketBuffer data) {
		if (Versions.isClient()) {
			onPacketRecievedClient(channel, data);
		}
	}
	
	protected void register() {
		BLMain.LOGGER_MAIN.logInfo("Registering Blazeloader packet channel...");
		SimpleFunc<PluginChannels, Void> addPluginChannelsFor = Reflect.lookupMethod(PluginChannels.class, Void.class, "addPluginChannelsFor", CommonPluginChannelListener.class);
		if (addPluginChannelsFor.valid()) {
			try {
				addPluginChannelsFor.apply(LiteLoader.getServerPluginChannels(), this);
				if (Versions.isClient()) {
					addPluginChannelsFor.apply(LiteLoader.getClientPluginChannels(), this);
				}
				BLMain.LOGGER_MAIN.logInfo("Packet channel registration complete.");
			} catch (Throwable e) {
				BLMain.LOGGER_MAIN.logError("Exception whilst registering BL packet channel!", e);
			}
		}
	}
}
