package com.blazeloader.bl.main;

import com.blazeloader.api.particles.ParticlesRegister;
import com.blazeloader.event.handlers.EventHandler;
import com.blazeloader.event.handlers.client.EventHandlerClient;
import com.blazeloader.event.handlers.client.ResourcesEventHandler;
import com.blazeloader.util.version.Versions;
import com.mumfrey.liteloader.api.CoreProvider;
import com.mumfrey.liteloader.common.GameEngine;
import com.mumfrey.liteloader.common.Resources;
import com.mumfrey.liteloader.core.LiteLoaderMods;
import com.mumfrey.liteloader.resources.InternalResourcePack;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.world.World;

/**
 * BlazeLoader CoreProvider
 */
public class BlazeLoaderCoreProvider implements CoreProvider {
    private static final BlazeLoaderCoreProvider instance = new BlazeLoaderCoreProvider();
    
    public static BlazeLoaderCoreProvider instance() {
    	return instance;
    }
    
    private GameEngine gameEngine;
    
    public GameEngine getGameEngine() {
        return gameEngine;
    }
    
    private BlazeLoaderCoreProvider() {
    }
    
    @Override
    public void onInit() {
    }
    
    @Override
    public void onPostInit(GameEngine<?, ?> engine) {
        gameEngine = engine;
        if (engine.isClient()) {
        	ResourcesEventHandler.initialiseResources((Resources<?, InternalResourcePack>)engine.getResources());
        }
    }
    
    @Override
    public void onPostInitComplete(LiteLoaderMods mods) {
    	EventHandler.eventStart();
    }
    
    @Override
    public void onStartupComplete() {
		ParticlesRegister.instance().preInit();
    	BLPacketChannels.instance().register();
    }
    
    @Override
    public void onJoinGame(INetHandler netHandler, S01PacketJoinGame loginPacket) {
    	if (Versions.isClient()) {
    		EventHandlerClient.overrideClientJoinGame(netHandler, loginPacket);
    	}
    }
    
    @Override
    public void onPostRender(int mouseX, int mouseY, float partialTicks) {

    }
    
    @Override
    public void onShutDown() {
        EventHandler.eventEnd();
    }
    
    @Override
    public void onTick(boolean clock, float partialTicks, boolean inGame) {
    	BLMain.instance().tick(clock, partialTicks, inGame);
        EventHandler.eventTick();
    }
    
    @Override
    public void onWorldChanged(World world) {
    	if (Versions.isClient()) {
    		EventHandlerClient.overrideWorldChanged(world);
    	}
    }
}
