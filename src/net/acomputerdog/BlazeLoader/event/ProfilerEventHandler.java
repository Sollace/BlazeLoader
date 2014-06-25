package net.acomputerdog.BlazeLoader.event;

import com.mumfrey.liteloader.LiteMod;
import net.acomputerdog.BlazeLoader.mod.BLMod;

/**
 * Interface for mods that handle profiler events
 */
public interface ProfilerEventHandler extends BLMod {
    /**
     * Called when a profiler section is started.  Mods are notified BEFORE profiler.
     *
     * @param sectionName Name of the profiler section started.
     */
    public void eventProfilerStart(String sectionName);

    /**
     * Called when a profiler section is ended.  Mods are notified AFTER profiler.
     *
     * @param sectionName Name of the profiler section ended.
     */
    public void eventProfilerEnd(String sectionName);
}