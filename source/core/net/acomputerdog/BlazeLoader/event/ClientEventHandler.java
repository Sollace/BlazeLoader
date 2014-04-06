package net.acomputerdog.BlazeLoader.event;

import net.minecraft.client.gui.GuiScreen;

/**
 * Interface for mods that handle client-specific events
 */
public interface ClientEventHandler {

    /**
     * Called when a GUI is about to be displayed.
     *
     * @param oldGui  The current GUI.
     * @param newGui  The GUI being displayed.
     * @param allowed Set to true if the GUI will be displayed, false if another mod has disabled it.
     * @return Return true to allow the GUI, false to block it.
     */
    public boolean eventDisplayGui(GuiScreen oldGui, GuiScreen newGui, boolean allowed);
}