package com.blazeloader.event.handlers.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;

import com.blazeloader.api.client.ApiClient;
import com.blazeloader.api.gui.CreativeTabGui;
import com.blazeloader.event.handlers.EventHandler;
import com.blazeloader.event.listeners.args.ContainerOpenedEventArgs;
import com.blazeloader.event.listeners.client.ClientPlayerListener;
import com.blazeloader.event.listeners.client.ClientWorldListener;
import com.blazeloader.event.listeners.client.GuiListener;
import com.blazeloader.event.listeners.client.OverrideListener;
import com.blazeloader.event.listeners.client.ProfilerListener;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.core.event.HandlerList.ReturnLogicOp;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;

/**
 * Distributes game events to mods
 */
public class EventHandlerClient extends EventHandler {
    public static final HandlerList<GuiListener> guiEventClients = new HandlerList<GuiListener>(GuiListener.class);
    public static final HandlerList<OverrideListener> overrideEventClients = new HandlerList<OverrideListener>(OverrideListener.class, ReturnLogicOp.OR_BREAK_ON_TRUE);
    public static final HandlerList<ClientPlayerListener> playerEventClients = new HandlerList<ClientPlayerListener>(ClientPlayerListener.class);
    public static final HandlerList<ProfilerListener> profilerEventClients = new HandlerList<ProfilerListener>(ProfilerListener.class);
    public static final HandlerList<ClientWorldListener> worldEventClients = new HandlerList<ClientWorldListener>(ClientWorldListener.class);
    
    private static boolean eventDisplayGuiScreen = false;
    public static void eventDisplayGuiScreen(EventInfo<Minecraft> event, GuiScreen gui) {
    	if (!eventDisplayGuiScreen) {
	        eventDisplayGuiScreen = true;
	        Minecraft mc = event.getSource();
	    	if (CreativeTabs.creativeTabArray.length > 12 && gui instanceof GuiContainerCreative && !(gui instanceof CreativeTabGui)) {
	    		event.cancel();
	    		mc.displayGuiScreen(new CreativeTabGui(event.getSource().thePlayer));
	    		return;
	    	}
	        guiEventClients.all().onGuiOpen(mc, mc.currentScreen, gui);
	        eventDisplayGuiScreen = false;
    	}
    }
    
    public static void eventStartSection(EventInfo<Profiler> event, String name) {
        profilerEventClients.all().onSectionStart(event.getSource(), name);
    }

    public static void eventEndSection(EventInfo<Profiler> event) {
        Profiler prof = event.getSource();
        profilerEventClients.all().onSectionEnd(prof, prof.getNameOfLastSection());
    }

    public static void eventLoadWorld(EventInfo<Minecraft> event, WorldClient world, String message) {
        Minecraft mc = event.getSource();
        if (world != null) {
            worldEventClients.all().onWorldLoad(mc, world, message);
        } else {
            worldEventClients.all().onWorldUnload(mc, mc.theWorld, message);
        }
    }
    
    public static void eventFunc_151260_c(ReturnEventInfo<EntityTrackerEntry, Packet> event) {
    	EntityTrackerEntry entry = event.getSource();
    	if (!entry.trackedEntity.isDead) {
    		S0EPacketSpawnObject packet = null;
            for (OverrideListener mod : overrideEventClients) {
                S0EPacketSpawnObject modPacket = mod.onCreateSpawnPacket(entry.trackedEntity, packet != null);
                if (modPacket != null) packet = modPacket;
            }
            if (packet != null) {
            	event.setReturnValue(packet);
            }
        }
    }
    
    public static void eventTrackEntity(EventInfo<EntityTracker> event, Entity entity) {
    	if (!isInEvent) {
	    	isInEvent = true;
	    	boolean isHandled = false;
	        for (OverrideListener mod : overrideEventClients) {
	            isHandled |= mod.onAddEntityToTracker(event.getSource(), entity, isHandled);
	        }
	        isInEvent = false;
    	}
    }
    
    public static void eventHandleOpenWindow(EventInfo<INetHandlerPlayClient> event, S2DPacketOpenWindow packet) {
    	Minecraft gameController = Minecraft.getMinecraft();
    	PacketThreadUtil.checkThreadAndEnqueue(packet, event.getSource(), gameController);
        ContainerOpenedEventArgs args = new ContainerOpenedEventArgs(gameController.thePlayer, packet);
        if (overrideEventClients.all().onContainerOpened(gameController.thePlayer, args)) {
        	gameController.thePlayer.openContainer.windowId = packet.getWindowId();
        	event.cancel();
        }
    }
    
    public static void eventSpawnEffectParticle(ReturnEventInfo<EffectRenderer, EntityFX> event, int particleId, double x, double y, double z, double xOffset, double yOffset, double zOffset, int ... args) {
        EntityFX entity = overrideSpawnEffectParticle(particleId, x, y, z, xOffset, yOffset, zOffset, args);
        if (entity != null) {
        	event.getSource().addEffect(entity);
        	event.setReturnValue(entity);
        }
    }
    
    public static void eventSetPlayerSPHealth(EventInfo<EntityPlayerSP> event, float health) {
    	if (!event.getSource().isEntityAlive() || event.getSource().getHealth() <= 0) {
    		playerEventClients.all().onClientPlayerDeath();
    	}
    }
    
    public static EntityFX overrideSpawnEffectParticle(int particleId, double x, double y, double z, double xOffset, double yOffset, double zOffset, int ... args) {
        EntityFX entity = null;
        for (OverrideListener mod : overrideEventClients) {
            entity = mod.onSpawnParticle(particleId, x, y, z, zOffset, yOffset, zOffset, entity);
        }
        
        return entity;
    }
    
    public static void overrideClientJoinGame(INetHandler netHandler, S01PacketJoinGame loginPacket) {
        playerEventClients.all().onClientJoinGame(netHandler, loginPacket);
    }
    
    public static void overrideWorldChanged(World world) {
    	worldEventClients.all().onWorldChanged(world);
    }
    
    public static void eventHandleHeldItemChange(EventInfo<NetHandlerPlayClient> event, S09PacketHeldItemChange packet) {
    	if (inventoryEventHandlers.size() > 0) {
    		int index = packet.getHeldItemHotbarIndex();
	    	if (index >= 0 && index < InventoryPlayer.getHotbarSize()) {
	    		Minecraft mc = Minecraft.getMinecraft();
	    		if (mc != null && mc.thePlayer != null) {
					if (!inventoryEventHandlers.all().onSlotSelectionChanged(mc.thePlayer, mc.thePlayer.inventory.getStackInSlot(index), index)) {
						event.cancel();
					}
	    		}
	    	}
    	}
    }
    
    public static void eventHandleCollectItem(EventInfo<NetHandlerPlayClient> event, S0DPacketCollectItem packet) {
    	if (inventoryEventHandlers.size() > 0) {
	    	Minecraft mc = ApiClient.getClient();
	    	Entity owner = mc.theWorld.getEntityByID(packet.getEntityID());
	    	if (owner == null) owner = mc.thePlayer;
	    	Entity item = mc.theWorld.getEntityByID(packet.getCollectedItemEntityID());
	    	if (item != null) {
	    		int amount = 1;
	    		if (item instanceof EntityItem) {
	    			amount = ((EntityItem)item).getEntityItem().stackSize;
	    		}
	    		inventoryEventHandlers.all().onItemPickup(owner, item, amount);
	    	}
    	}
    }
}
