package com.blazeloader.event.transformers.client;

import com.blazeloader.bl.main.BLMain;
import com.blazeloader.event.transformers.BLEventInjectionTransformer;

/**
 * Injects events into MC classes
 */
public class BLEventInjectionTransformerClient extends BLEventInjectionTransformer {

    /**
     * Subclasses should register events here
     */
    @Override
    protected void addEvents() {
        try {
            addBLEvent(EventSide.CLIENT, "net.minecraft.client.Minecraft.loadWorld (Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V");
            addBLEvent(EventSide.CLIENT, "net.minecraft.profiler.Profiler.startSection (Ljava/lang/String;)V");
            addBLEvent(EventSide.CLIENT, "net.minecraft.profiler.Profiler.endSection ()V");
            addBLEvent(EventSide.CLIENT, "net.minecraft.client.Minecraft.displayGuiScreen (Lnet/minecraft/client/gui/GuiScreen;)V");
            addBLEvent(EventSide.INTERNAL, "net.minecraft.client.ClientBrandRetriever.getClientModName ()Ljava/lang/String;", beforeReturn);
        } catch (Exception e) {
            e.printStackTrace();
            BLMain.instance().shutdown("A fatal exception occurred while injecting BlazeLoader client events!  BlazeLoader will not be able to run!", -1);
        }
    }

}