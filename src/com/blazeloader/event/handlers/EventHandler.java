package com.blazeloader.event.handlers;

import java.net.SocketAddress;

import com.blazeloader.api.ApiServer;
import com.blazeloader.api.entity.properties.EntityPropertyManager;
import com.blazeloader.api.world.gen.UnpopulatedChunksQ;
import com.blazeloader.event.listeners.*;
import com.blazeloader.event.listeners.args.BlockEventArgs;
import com.blazeloader.event.listeners.args.EntitySpawnEventArgs;
import com.blazeloader.event.listeners.args.FallEventArgs;
import com.blazeloader.event.listeners.args.InventoryEventArgs;
import com.blazeloader.event.listeners.args.LoginEventArgs;
import com.mojang.authlib.GameProfile;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.core.event.HandlerList.ReturnLogicOp;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

/**
 * Side-independent event handler
 */
public class EventHandler {
	/**
	 * Used where there is a chance a mod can call the method that triggers the event.
	 * Added to prevent infinite recursion inside an event.
	 */
	protected static boolean isInEvent = false;
	
    public static final HandlerList<StartupListener> modEventHandlers = new HandlerList<StartupListener>(StartupListener.class);
    public static final HandlerList<InventoryListener> inventoryEventHandlers = new HandlerList<InventoryListener>(InventoryListener.class, ReturnLogicOp.OR_BREAK_ON_TRUE);
    public static final HandlerList<TickListener> tickEventHandlers = new HandlerList<TickListener>(TickListener.class);
    public static final HandlerList<WorldListener> worldEventHandlers = new HandlerList<WorldListener>(WorldListener.class);
    public static final HandlerList<BlockChangedListener> blockEventHandlers = new HandlerList<BlockChangedListener>(BlockChangedListener.class, ReturnLogicOp.AND_BREAK_ON_FALSE);
    public static final HandlerList<PlayerListener> playerEventHandlers = new HandlerList<PlayerListener>(PlayerListener.class, ReturnLogicOp.OR_BREAK_ON_TRUE);
    public static final HandlerList<ChunkListener> chunkEventHandlers = new HandlerList<ChunkListener>(ChunkListener.class);
    public static final HandlerList<EntityConstructingListener> entityEventHandlers = new HandlerList<EntityConstructingListener>(EntityConstructingListener.class);
    public static final HandlerList<EntityTrackingListener> entityTrackings = new HandlerList<EntityTrackingListener>(EntityTrackingListener.class, ReturnLogicOp.OR_BREAK_ON_TRUE);

    public static void eventTick() {
        tickEventHandlers.all().onTick();
    }

    public static void eventStart() {
        modEventHandlers.all().start();
    }

    public static void eventEnd() {
        modEventHandlers.all().stop();
    }
    
    public static void eventInit(ReturnEventInfo<WorldServer, World> event) {
    	worldEventHandlers.all().onWorldInit(event.getSource());
    }
    
    private static boolean eventSpawnEntityInWorld = false;
    public static void eventSpawnEntityInWorld(ReturnEventInfo<World, Boolean> event, Entity entity) {
    	if (!eventSpawnEntityInWorld && worldEventHandlers.size() > 0) {
    		if (!(entity instanceof EntityPlayer)) {
    			eventSpawnEntityInWorld = true;
    			EntitySpawnEventArgs args = new EntitySpawnEventArgs(entity);
    			worldEventHandlers.all().onEntitySpawned(event.getSource(), args);
    			if (args.getEntityChanged()) {
    				entity = args.getEntity();
    			}
    			if (args.getForced() != entity.forceSpawn) {
    				entity.forceSpawn = args.getForced();
    				event.setReturnValue(event.getSource().spawnEntityInWorld(entity));
    				entity.forceSpawn = !entity.forceSpawn;
    			} else if (args.getEntityChanged()) {
    				event.setReturnValue(event.getSource().spawnEntityInWorld(entity));
    			}
    			eventSpawnEntityInWorld = false;
    		}
    	}
    }
    
    private static boolean eventSetBlockState = false;
    public static void eventSetBlockState(ReturnEventInfo<World, Boolean> event, BlockPos pos, IBlockState state, int flag) {
    	if (!eventSetBlockState && blockEventHandlers.size() > 0) {
    		World w = event.getSource();
    		if (w.isValid(pos)) {
	    		IBlockState old = event.getSource().getBlockState(pos);
	    		if (!old.equals(state)) {
	    			BlockEventArgs args = new BlockEventArgs(old, state);
		    		eventSetBlockState = true;
		    		blockEventHandlers.all().onBlockChanged(w, pos, args);
		    		if (args.isCancelled()) {
		    			event.setReturnValue(false);
		    		} else if (!state.equals(args.getNewState())) {
		    			event.setReturnValue(w.setBlockState(pos, args.getNewState()));
		    		}
		    		eventSetBlockState = false;
	    		}
    		}
    	}
    }
    
    public static void eventTryHarvestBlock(ReturnEventInfo<ItemInWorldManager, Boolean> event, BlockPos pos) {
    	if (event.getReturnValue() && blockEventHandlers.size() > 0) {
    		World w = event.getSource().theWorld;
    		IBlockState state = w.getBlockState(pos);
    		blockEventHandlers.all().onBreakBlock(event.getSource().thisPlayerMP, w, state, pos);
    	}
    }
    
    public static void eventOnItemUse(ReturnEventInfo<ItemBlock, Boolean> event, ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
    	if (event.getReturnValue() && blockEventHandlers.size() > 0) {
    		blockEventHandlers.all().onPlaceBlock(player, world, world.getBlockState(pos), pos);
    	}
    }
    
    public static void eventPlayerLoggedIn(EventInfo<ServerConfigurationManager> event, EntityPlayerMP player) {
        playerEventHandlers.all().onPlayerLoginMP(event.getSource(), player);
    }

    public static void eventPlayerLoggedOut(EventInfo<ServerConfigurationManager> event, EntityPlayerMP player) {
        playerEventHandlers.all().onPlayerLogoutMP(event.getSource(), player);
    }

    public static <ReturnType> void eventRecreatePlayerEntity(ReturnEventInfo<ServerConfigurationManager, ReturnType> event, EntityPlayerMP oldPlayer, int dimension, boolean didWin) {
        playerEventHandlers.all().onPlayerRespawnMP(event.getSource(), oldPlayer, dimension, !didWin);
    }
    
    public static void eventAllowUserToConnect(ReturnEventInfo<ServerConfigurationManager, String> event, SocketAddress address, GameProfile profile) {
    	if (playerEventHandlers.size() > 0) {
    		String errorMessage = event.getReturnValue();
    		if (errorMessage == null) {
    			LoginEventArgs args = new LoginEventArgs(profile);
    			for (PlayerListener i : playerEventHandlers) {
    				i.onPlayerTryLoginMP(args);
    				if (args.isBlocked()) {
        				event.setReturnValue(args.getMessage());
        				return;
        			}
    			}
    		}
    	}
    }
    
    public static void eventOnChunkLoad(EventInfo<Chunk> event) {
        Chunk chunk = event.getSource();
        if (!chunk.isTerrainPopulated()) {
            UnpopulatedChunksQ.instance().push(chunk);
        }
        chunkEventHandlers.all().onChunkLoad(chunk);
    }

    public static void eventOnChunkUnload(EventInfo<Chunk> event) {
        Chunk chunk = event.getSource();
        UnpopulatedChunksQ.instance().pop(chunk);
        chunkEventHandlers.all().onChunkUnload(chunk);
    }
    
    public static void initEntity(EventInfo<Entity> event, World w) {
    	if (!(event.getSource() instanceof EntityPlayer)) {
	    	entityEventHandlers.all().onEntityConstructed(event.getSource());
	    	EntityPropertyManager.instance().entityinit(event.getSource());
    	}
    }
    
    public static void initEntityPlayer(EventInfo<EntityPlayer> event, World w, GameProfile profile) {
    	entityEventHandlers.all().onEntityConstructed(event.getSource());
    	EntityPropertyManager.instance().entityinit(event.getSource());
    }
    
    public static void eventCollideWithPlayer(EventInfo<EntityPlayer> event, Entity entity) {
    	if (playerEventHandlers.size() > 0 && !playerEventHandlers.all().onEntityCollideWithPlayer(entity, event.getSource())) {
    		event.cancel();
    	}
    }
    
    public static void eventDoBlockCollisions(EventInfo<Entity> event) {
    	if (playerEventHandlers.size() > 0 && event.getSource() instanceof EntityPlayer) {
    		EntityPlayer player = (EntityPlayer)event.getSource();
    		if (player.isEntityAlive() && !player.isPlayerSleeping() && !player.noClip) {
	    		BlockPos var1 = new BlockPos(player.getEntityBoundingBox().minX + 0.001D, player.getEntityBoundingBox().minY + 0.001D, player.getEntityBoundingBox().minZ + 0.001D);
	            BlockPos var2 = new BlockPos(player.getEntityBoundingBox().maxX - 0.001D, player.getEntityBoundingBox().maxY - 0.001D, player.getEntityBoundingBox().maxZ - 0.001D);
	            if (player.worldObj.isAreaLoaded(var1, var2)) {
	                for (int var3 = var1.getX(); var3 <= var2.getX(); ++var3) {
	                    for (int var4 = var1.getY(); var4 <= var2.getY(); ++var4) {
	                        for (int var5 = var1.getZ(); var5 <= var2.getZ(); ++var5) {
	                            BlockPos pos = new BlockPos(var3, var4, var5);
	                            IBlockState state = player.worldObj.getBlockState(pos);
	                            if (state.getBlock().isVisuallyOpaque()) {
	                            	playerEventHandlers.all().onPlayerCollideWithBlock(state, pos, player);
	                            }
	                        }
	                    }
	                }
	            }
    		}
    	}
    }
    
    private static boolean inEvent = false;
    public static void eventSetCurrentItem(EventInfo<InventoryPlayer> event, Item itemIn, int targetEntityId, boolean hasSubTypes, boolean isCreativeMode) {
    	if (inventoryEventHandlers.size() > 0) {
    		if (!inEvent) {
    			inEvent = true;
				InventoryPlayer inventory = event.getSource();
				inventoryEventHandlers.all().onSlotSelectionChanged(inventory.player, inventory.getCurrentItem(), inventory.currentItem);
				inEvent = false;
    		}
    	}
    }
    
    public static void eventChangeCurrentItem(EventInfo<InventoryPlayer> event, int increment) {
    	if (inventoryEventHandlers.size() > 0) {
	    	InventoryPlayer inventory = event.getSource();
	    	int newIndex = event.getSource().currentItem + (increment > 0 ? 1 : increment < 0 ? -1 : 0);
	    	if (newIndex > -1) {
	    		for (newIndex -= increment; newIndex < 0; newIndex += InventoryPlayer.getHotbarSize());
	    		newIndex = newIndex % InventoryPlayer.getHotbarSize();
				if (!inventoryEventHandlers.all().onSlotSelectionChanged(inventory.player, inventory.getStackInSlot(newIndex), newIndex)) {
					event.cancel();
				}
	    	}
    	}
    }
    
    /*public static void eventOnItemPickup(EventInfo<EntityLivingBase> event, Entity itemEntity, int amount) {
    	if (inventoryEventHandlers.size() > 0 && !itemEntity.isDead && !event.getSource().worldObj.isRemote) {
    		inventoryEventHandlers.all().onItemPickup(event.getSource(), itemEntity, amount);
    	}
    }*/
    
    public static void eventUpdateEquipmentIfNeeded(EventInfo<EntityLiving> event, EntityItem entityItem) {
    	if (inventoryEventHandlers.size() > 0) {
	    	ItemStack pickedUp = entityItem.getEntityItem();
	    	InventoryEventArgs args = new InventoryEventArgs(pickedUp);
	    	inventoryEventHandlers.all().onEntityEquipItem(event.getSource(), entityItem, args);
	    	if (args.isCancelled()) {
	    		event.cancel();
	    	} else {
	    		if (args.isDirty()) {
	    			entityItem.setEntityItemStack(args.getItemStack());
	    		}
	    	}
    	}
    }
    
    public static void eventEntityDropItem(ReturnEventInfo<Entity, EntityItem> event, ItemStack droppedItem, float yOffset) {
    	if (!isInEvent && droppedItem != null && droppedItem.stackSize > 0) {
    		if (inventoryEventHandlers.size() > 0) {
	    		Entity entity = event.getSource();
	    		InventoryEventArgs args = new InventoryEventArgs(droppedItem);
	    		inventoryEventHandlers.all().onDropItem(entity, false, false, args);
		    	if (args.isCancelled()) {
		    		event.setReturnValue(null);
		    	} else {
		    		if (args.isDirty()) {
		    			isInEvent = true;
		    			event.setReturnValue(entity.entityDropItem(args.getItemStack(), yOffset));
		    			isInEvent = false;
		    		}
		    	}
	    	}
    	}
    }
    
    //Because otherwise items held in an inventory get deleted when a mod cancels the drop.
    private static ItemStack savedHeldItemStack;
    public static void eventDropItem(ReturnEventInfo<EntityPlayer, EntityItem> event, ItemStack droppedItem, boolean dropAround, boolean traceItem) {
    	savedHeldItemStack = null;
    	if (!isInEvent && droppedItem != null && droppedItem.stackSize > 0) {
    		if (inventoryEventHandlers.size() > 0) {
	    		EntityPlayer player = event.getSource();
	    		ItemStack held = player.inventory.getItemStack();
	    		InventoryEventArgs args = new InventoryEventArgs(droppedItem);
	    		inventoryEventHandlers.all().onDropItem(player, dropAround, traceItem, args);
		    	if (args.isCancelled()) {
		    		event.setReturnValue(null);
		    		if (held != null) {
			    		if (!held.equals(droppedItem)) {
			    			held.stackSize += droppedItem.stackSize;
			    		} else {
			    			savedHeldItemStack = held;
			    		}
		    		}
		    	} else {
		    		if (args.isDirty()) {
		    			isInEvent = true;
		    			event.setReturnValue(player.dropItem(args.getItemStack(), dropAround, traceItem));
		    			isInEvent = false;
		    		}
		    	}
    		}
    	}
    }
    
    //Now we then have to put the item back
    public static void eventSlotClick(ReturnEventInfo<Container, ItemStack> event, int slotId, int clickedButton, int mode, EntityPlayer player) {
    	if (savedHeldItemStack != null) {
	    	if (event.getReturnValue() == null && player.inventory.getItemStack() == null) {
	    		player.inventory.setItemStack(savedHeldItemStack);
	    		savedHeldItemStack = null;
	    	}
    	}
    }
    
    public static void eventDropOneItem(ReturnEventInfo<EntityPlayer, EntityItem> event, boolean dropAll) {
    	if (inventoryEventHandlers.size() > 0) {
	    	EntityPlayer player = event.getSource();
	    	ItemStack droppedItem = player.inventory.getCurrentItem().copy();
	    	if (droppedItem != null) {
		    	if (!dropAll) droppedItem.stackSize = 1;
		    	InventoryEventArgs args = new InventoryEventArgs(droppedItem);
		    	inventoryEventHandlers.all().onDropOneItem(player, dropAll, args);
		    	if (args.isCancelled()) {
		    		event.setReturnValue(null);
		    	}
		    	if (args.isDirty()) {
		    		event.setReturnValue(player.dropItem(args.getItemStack(), false, true));
		    		player.inventory.decrStackSize(player.inventory.currentItem, droppedItem.stackSize);
		    	}
	    	}
    	}
    }
    
    public static void eventFall(EventInfo<EntityPlayer> event, float distance, float multiplier) {
    	if (playerEventHandlers.size() > 0) {
    		if (!isInEvent) {
    			FallEventArgs args = new FallEventArgs(distance, multiplier);
    			playerEventHandlers.all().onPlayerFall(event.getSource(), args);
    			if (!args.isCancelled()) {
					distance = args.getFallDistance();
					multiplier = args.getDamageMultiplier();
					isInEvent = true;
					event.getSource().fall(distance, multiplier);
					isInEvent = false;
    			}
    			event.cancel();
        	}
    	}
    }
    
    public static void initS0DPacketCollectItem(EventInfo<S0DPacketCollectItem> event, int itemId, int entityId) {
    	if (inventoryEventHandlers.size() > 0) {
	    	MinecraftServer server = ApiServer.getServer();
	    	Entity owner = null;
	    	for (WorldServer i : server.worldServers) {
	    		owner = i.getEntityByID(entityId);
	    		if (owner != null) break;
	    	}
	    	Entity item = null;
	    	for (WorldServer i : server.worldServers) {
	    		item = i.getEntityByID(itemId);
	    		if (item != null) break;
	    	}
	    	if (item != null && owner != null) {
	    		int amount = 1;
	    		if (item instanceof EntityItem) {
	    			amount = ((EntityItem)item).getEntityItem().stackSize;
	    		}
	    		inventoryEventHandlers.all().onItemPickup(owner, item, amount);
	    	}
    	}
    }
    
    
    public static void eventFunc_151260_c(ReturnEventInfo<EntityTrackerEntry, Packet> event) {
    	EntityTrackerEntry entry = event.getSource();
    	if (!entry.trackedEntity.isDead) {
    		S0EPacketSpawnObject packet = null;
            for (EntityTrackingListener mod : entityTrackings) {
                S0EPacketSpawnObject modPacket = mod.onCreateSpawnPacket(entry.trackedEntity, packet != null);
                if (modPacket != null) packet = modPacket;
            }
            if (packet != null) event.setReturnValue(packet);
        }
    }
    
    public static void eventTrackEntity(EntityTracker sender, Entity entity) {
    	if (!isInEvent) {
	    	isInEvent = true;
	    	boolean isHandled = false;
	        for (EntityTrackingListener mod : entityTrackings) {
	            isHandled |= mod.onAddEntityToTracker(sender, entity, isHandled);
	        }
	        isInEvent = false;
    	}
    }
}
