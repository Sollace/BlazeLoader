package com.blazeloader.event.handlers.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.api.client.ApiClient;
import com.blazeloader.api.gui.CreativeTabGui;
import com.blazeloader.event.handlers.EventHandler;
import com.blazeloader.event.listeners.args.ContainerOpenedEventArgs;
import com.blazeloader.event.listeners.client.ClientPlayerListener;
import com.blazeloader.event.listeners.client.ClientWorldListener;
import com.blazeloader.event.listeners.client.GuiListener;
import com.blazeloader.event.listeners.client.OverrideListener;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.core.event.HandlerList.ReturnLogicOp;

/**
 * Distributes game events to mods
 */
public class EventHandlerClient extends EventHandler {
    public static final HandlerList<GuiListener> guiEventClients = new HandlerList<GuiListener>(GuiListener.class);
    public static final HandlerList<OverrideListener> overrideEventClients = new HandlerList<OverrideListener>(OverrideListener.class, ReturnLogicOp.OR_BREAK_ON_TRUE);
    public static final HandlerList<ClientPlayerListener> playerEventClients = new HandlerList<ClientPlayerListener>(ClientPlayerListener.class);
    public static final HandlerList<ClientWorldListener> worldEventClients = new HandlerList<ClientWorldListener>(ClientWorldListener.class);
    
    private static boolean eventDisplayGuiScreen = false;
    public static void eventDisplayGuiScreen(Minecraft sender, CallbackInfo info, GuiScreen gui) {
    	GuiScreen replacement = getCreativeGui(sender, gui);
        if (replacement != gui) {
        	info.cancel();
        	sender.displayGuiScreen(replacement);
        	return;
        }
    	
    	if (!eventDisplayGuiScreen) {
	        eventDisplayGuiScreen = true;
	        guiEventClients.all().onGuiOpen(sender, sender.currentScreen, gui);
	        eventDisplayGuiScreen = false;
    	}
    }
    
    public static GuiScreen getCreativeGui(Minecraft sender, GuiScreen original) {
    	if (CreativeTabs.creativeTabArray.length > 12 && original instanceof GuiContainerCreative && !(original instanceof CreativeTabGui)) {
    		return new CreativeTabGui(sender.thePlayer);
    	}
    	return original;
    }

    public static void eventLoadWorld(Minecraft sender, WorldClient world, String message) {
        if (world != null) {
            worldEventClients.all().onWorldLoad(sender, world, message);
        } else {
            worldEventClients.all().onWorldUnload(sender, sender.theWorld, message);
        }
    }
    
    public static void eventHandleOpenWindow(INetHandlerPlayClient sender, CallbackInfo info, SPacketOpenWindow packet) {
    	Minecraft gameController = Minecraft.getMinecraft();
    	PacketThreadUtil.checkThreadAndEnqueue(packet, sender, gameController);
        ContainerOpenedEventArgs args = new ContainerOpenedEventArgs(gameController.thePlayer, packet);
        if (overrideEventClients.all().onContainerOpened(gameController.thePlayer, args)) {
        	gameController.thePlayer.openContainer.windowId = packet.getWindowId();
        	info.cancel();
        }
    }
    
    public static void eventSpawnEffectParticle(EffectRenderer sender, CallbackInfoReturnable<EntityFX> info, int particleId, double x, double y, double z, double xOffset, double yOffset, double zOffset, int ... args) {
        EntityFX entity = overrideSpawnEffectParticle(particleId, x, y, z, xOffset, yOffset, zOffset, args);
        if (entity != null) {
        	sender.addEffect(entity);
        	info.setReturnValue(entity);
        }
    }
    
    public static void eventSetPlayerSPHealth(EntityPlayerSP sender, float health) {
    	if (!sender.isEntityAlive() || sender.getHealth() <= 0) {
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
    
    public static void overrideClientJoinGame(INetHandler netHandler, SPacketJoinGame loginPacket) {
        playerEventClients.all().onClientJoinGame(netHandler, loginPacket);
    }
    
    public static void overrideWorldChanged(World world) {
    	worldEventClients.all().onWorldChanged(world);
    }
    
    public static void eventHandleHeldItemChange(CallbackInfo info, SPacketHeldItemChange packet) {
    	if (inventoryEventHandlers.size() > 0) {
    		int index = packet.getHeldItemHotbarIndex();
	    	if (index >= 0 && index < InventoryPlayer.getHotbarSize()) {
	    		Minecraft mc = Minecraft.getMinecraft();
	    		if (mc != null && mc.thePlayer != null) {
					if (!inventoryEventHandlers.all().onSlotSelectionChanged(mc.thePlayer, mc.thePlayer.inventory.getStackInSlot(index), index)) {
						info.cancel();
					}
	    		}
	    	}
    	}
    }
    
    public static void eventHandleCollectItem(SPacketCollectItem packet) {
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
