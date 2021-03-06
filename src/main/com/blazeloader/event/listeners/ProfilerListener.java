package com.blazeloader.event.listeners;

import com.blazeloader.bl.mod.BLMod;

import net.minecraft.profiler.Profiler;

/**
 * Interface for mods that handle profiler events
 */
public interface ProfilerListener extends BLMod {
    /**
     * Called when a profiler section is started.  Mods are notified BEFORE profiler.
     *
     * @param profiler    The profiler who's section is starting.
     * @param sectionName Name of the profiler section started.
     */
    public void onSectionStart(Profiler profiler, String sectionName);

    /**
     * Called when a profiler section is ended.  Mods are notified BEfORE profiler.
     *
     * @param profiler    The profiler who's section is starting.
     * @param sectionName Name of the profiler section ended.
     */
    public void onSectionEnd(Profiler profiler, String sectionName);
}
