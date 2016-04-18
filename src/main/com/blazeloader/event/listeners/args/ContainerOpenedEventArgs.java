package com.blazeloader.event.listeners.args;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

/**
 * Contains args for a ContainerOpenedEvent
 */
public class ContainerOpenedEventArgs {
	public final boolean hasSlots;
	public final int slotsCount;
	
	public final String inventoryId;
	public final IChatComponent inventoryTitle;

    public final int posX;
    public final int posY;
    public final int posZ;

    public ContainerOpenedEventArgs(EntityPlayer player, S2DPacketOpenWindow packet) {
        hasSlots = packet.hasSlots();
        slotsCount = packet.getSlotCount();
        inventoryId = packet.getGuiId();
        inventoryTitle = packet.getWindowTitle();
        
        posX = MathHelper.floor_double(player.posX);
        posY = MathHelper.floor_double(player.posY);
        posZ = MathHelper.floor_double(player.posZ);
    }
}