package net.acomputerdog.BlazeLoader.transformers;

import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;
import net.acomputerdog.BlazeLoader.util.obf.BLMethodInfo;
import net.acomputerdog.BlazeLoader.util.obf.BLOBF;

/**
 * Injects events into MC classes
 */
public class BLEventInjectionTransformer extends EventInjectionTransformer {
    private static final String EVENT_HANDLER = "net.acomputerdog.BlazeLoader.event.EventHandler";
    private static final InjectionPoint methodHead = new MethodHead();

    /**
     * Subclasses should register events here
     */
    @Override
    protected void addEvents() {
        try {
            //BLMethodInfo loadWorld = BLMethodInfo.create(BLOBF.getMethodMCP("net.minecraft.client.Minecraft.loadWorld (Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V"));
            //BLMethodInfo startSection = BLMethodInfo.create(BLOBF.getMethodMCP("net.minecraft.profiler.Profiler.startSection (Ljava/lang/String;)V"));
            //BLMethodInfo endSection = BLMethodInfo.create(BLOBF.getMethodMCP("net.minecraft.profiler.Profiler.endSection ()V").getValue());
            //BLMethodInfo showGUI = BLMethodInfo.create(BLOBF.getMethodMCP("net.minecraft.client.Minecraft.displayGuiScreen (Lnet/minecraft/client/gui/GuiScreen;)V"));

            this.addBLEvent(BLMethodInfo.create(BLOBF.getMethodMCP("net.minecraft.client.Minecraft.loadWorld (Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V")));
            this.addBLEvent(BLMethodInfo.create(BLOBF.getMethodMCP("net.minecraft.profiler.Profiler.startSection (Ljava/lang/String;)V")));
            this.addBLEvent(BLMethodInfo.create(BLOBF.getMethodMCP("net.minecraft.profiler.Profiler.endSection ()V").getValue()));
            this.addBLEvent(BLMethodInfo.create(BLOBF.getMethodMCP("net.minecraft.client.Minecraft.displayGuiScreen (Lnet/minecraft/client/gui/GuiScreen;)V")));
        } catch (Exception e) {
            System.err.println("A fatal exception occurred while injecting BlazeLoader!  BlazeLoader will not be able to run!");
            throw new RuntimeException("Exception injecting BlazeLoader!", e);
        }
    }

    private void addBLEvent(BLMethodInfo method) {
        this.addBLEvent(method, methodHead);
    }

    private void addBLEvent(BLMethodInfo method, InjectionPoint injectionPoint) {
        String name = method.getSimpleName();
        this.addEvent(Event.getOrCreate("BL." + name, false), method, injectionPoint).addListener(new MethodInfo(EVENT_HANDLER, "event" + capitaliseFirst(name)));
    }

    private static String capitaliseFirst(String str) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return str;
        }
        String firstChar = String.valueOf(str.charAt(0)).toUpperCase();
        return firstChar.concat(str.substring(1, str.length()));
    }
}
