package com.blazeloader.event.transformers;

import com.blazeloader.bl.exception.InvalidEventException;
import com.blazeloader.bl.main.BLMain;
import com.blazeloader.bl.obf.BLMethodInfo;
import com.blazeloader.bl.obf.BLOBF;
import com.blazeloader.bl.obf.BLObfProvider;
import com.blazeloader.bl.obf.OBFLevel;
import com.mojang.authlib.GameProfile;
import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.BeforeInvoke;
import com.mumfrey.liteloader.transformers.event.inject.BeforeReturn;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;

/**
 * Side-independent event injector
 */
public class BLEventInjectionTransformer extends EventInjectionTransformer {
    protected static final InjectionPoint methodHead = new MethodHead();
    protected static final InjectionPoint beforeReturn = new BeforeReturn();
    
    protected void addBLAccessor(String className) {
    	addAccessor("com.blazeloader.api.privileged.I" + className, BLObfProvider.create(className));
    }
    
    protected void addBLConstructorEvent(EventSide side, String clazz, Object[] paramaterTypes) {
    	addBLConstructorEvent(side, clazz, paramaterTypes, methodHead);
    }
    
    protected void addBLConstructorEvent(EventSide side, String clazz, Object[] paramaterTypes, InjectionPoint injectionPoint) {
    	BLOBF clas = BLOBF.getClass(clazz, OBFLevel.MCP);
    	String name = clas.simpleMcp;
    	addEvent(Event.getOrCreate("BL.<init>." + name, true), new MethodInfo(clas, Obf.constructor, Void.TYPE, paramaterTypes), injectionPoint).addListener(new MethodInfo(side.getHandler(), "init" + capitaliseFirst(name)));
    }
    
    protected void addBLEvent(EventSide side, String method) {
        addBLEvent(side, method, methodHead);
    }
    
    protected void addBLEvent(EventSide side, BLOBF obf) {
    	addBLEvent(side, BLMethodInfo.create(obf), methodHead);
    }
    
    public void addBLEvent(EventSide side, String method, InjectionPoint injectionPoint) {
        addBLEvent(side, BLOBF.getMethod(method, OBFLevel.MCP), injectionPoint);
    }
    
    public void addBLEvent(EventSide side, BLOBF obf, InjectionPoint injectionPoint) {
        addBLEvent(side, BLMethodInfo.create(obf), injectionPoint);
    }
    
    protected void addBLEvent(EventSide side, BLMethodInfo method, InjectionPoint injectionPoint) {
        if (method == null || injectionPoint == null) {
            throw new InvalidEventException(side.toString(), method, injectionPoint);
        }
        String name = method.getSimpleName();
        addEvent(Event.getOrCreate("BL." + name, true), method, injectionPoint).addListener(new MethodInfo(side.getHandler(), "event" + capitaliseFirst(name)));
    }

    protected static String capitaliseFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return String.valueOf(str.charAt(0)).toUpperCase().concat(str.substring(1, str.length()));
    }
    
    @Override
    protected void addEvents() {
        try {
        	addBLEvents();
        } catch (Exception e) {
        	e.printStackTrace();
            BLMain.instance().shutdown("A fatal exception occurred while injecting BlazeLoader events for side ( " + getSide().toUpperCase() + " )!\r\nThis error is not recoverable, BlazeLoader will now shut down the game.", -1);
        }
        try {
        	addBLAccessors();
        } catch (Exception e) {
        	e.printStackTrace();
            BLMain.instance().shutdown("A fatal exception occurred while injecting BlazeLoader accessors for side ( " + getSide().toUpperCase() + " )!\r\nThis error is not recoverable, BlazeLoader will now shut down the game.", -1);
        }
    }
    
    protected void addBLEvents() {
        addBLEvent(EventSide.SERVER, "net.minecraft.server.management.ServerConfigurationManager.playerLoggedIn (Lnet/minecraft/entity/player/EntityPlayerMP;)V");
        addBLEvent(EventSide.SERVER, "net.minecraft.server.management.ServerConfigurationManager.playerLoggedOut (Lnet/minecraft/entity/player/EntityPlayerMP;)V");
        addBLEvent(EventSide.SERVER, "net.minecraft.server.management.ServerConfigurationManager.recreatePlayerEntity (Lnet/minecraft/entity/player/EntityPlayerMP;IZ)Lnet/minecraft/entity/player/EntityPlayerMP;", beforeReturn);
        addBLEvent(EventSide.SERVER, "net.minecraft.server.management.ServerConfigurationManager.allowUserToConnect (Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Ljava/lang/String;", beforeReturn);
        
        addBLEvent(EventSide.SERVER, "net.minecraft.world.chunk.Chunk.onChunkLoad ()V", beforeReturn);
        addBLEvent(EventSide.SERVER, "net.minecraft.world.chunk.Chunk.onChunkUnload ()V", beforeReturn);
        addBLEvent(EventSide.SERVER, "net.minecraft.world.WorldServer.init ()Lnet/minecraft/world/World;", beforeReturn);
        addBLEvent(EventSide.SERVER, "net.minecraft.world.World.setBlockState (Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z");
        
        addBLEvent(EventSide.INTERNAL, "net.minecraft.world.World.doesBlockHaveSolidTopSurface (Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)Z", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.tileentity.TileEntityFurnace.getItemBurnTime (Lnet/minecraft/item/ItemStack;)I");
        
        addBLEvent(EventSide.INTERNAL, "net.minecraft.server.MinecraftServer.createNewCommandManager ()Lnet/minecraft/command/ServerCommandManager;", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.server.integrated.IntegratedServer.createNewCommandManager ()Lnet/minecraft/command/ServerCommandManager;", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.server.MinecraftServer.getServerModName ()Ljava/lang/String;", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.world.chunk.Chunk.populateChunk (Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;II)V", beforeReturn);
        
        addBLEvent(EventSide.INTERNAL, "net.minecraft.world.World.onEntityRemoved (Lnet/minecraft/entity/Entity;)V");
		addBLEvent(EventSide.INTERNAL, "net.minecraft.entity.Entity.writeToNBT (Lnet/minecraft/nbt/NBTTagCompound;)V");
        addBLEvent(EventSide.INTERNAL, "net.minecraft.entity.Entity.readFromNBT (Lnet/minecraft/nbt/NBTTagCompound;)V");
        addBLEvent(EventSide.INTERNAL, "net.minecraft.entity.Entity.copyDataFromOld (Lnet/minecraft/entity/Entity;)V", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.entity.Entity.addEntityCrashInfo (Lnet/minecraft/crash/CrashReportCategory;)V", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.entity.Entity.onUpdate ()V", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.entity.player.EntityPlayer.clonePlayer (Lnet/minecraft/entity/player/EntityPlayer;Z)V", beforeReturn);
        
        addBLEvent(EventSide.INTERNAL, "net.minecraft.entity.EntityTracker.trackEntity (Lnet/minecraft/entity/Entity;)V");
        addBLEvent(EventSide.INTERNAL, "net.minecraft.entity.EntityTrackerEntry.func_151260_c ()Lnet/minecraft/network/Packet;");
        
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.Entity.entityDropItem (Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/item/EntityItem;");
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.EntityLiving.updateEquipmentIfNeeded (Lnet/minecraft/entity/item/EntityItem;)V");
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.player.EntityPlayer.dropItem (Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/item/EntityItem;");
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.player.EntityPlayer.dropOneItem (Z)Lnet/minecraft/entity/item/EntityItem;");
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.player.EntityPlayer.collideWithPlayer (Lnet/minecraft/entity/Entity;)V");
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.player.InventoryPlayer.changeCurrentItem (I)V");
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.player.InventoryPlayer.setCurrentItem (Lnet/minecraft/item/Item;IZZ)V", beforeReturn);
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.player.EntityPlayer.fall (FF)V");
        addBLEvent(EventSide.SERVER, "net.minecraft.inventory.Container.slotClick (IIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", beforeReturn);
        
        addBLEvent(EventSide.SERVER, "net.minecraft.item.ItemBlock.onItemUse (Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;FFF)Z", beforeReturn);
        addBLEvent(EventSide.SERVER, "net.minecraft.server.management.ItemInWorldManager.tryHarvestBlock (Lnet/minecraft/util/BlockPos;)Z", beforeReturn);
        
        addBLConstructorEvent(EventSide.SERVER, "net.minecraft.network.play.server.S0DPacketCollectItem", new Object[] {int.class, int.class}, beforeReturn);
        
        addBLConstructorEvent(EventSide.SERVER, "net.minecraft.entity.Entity", new Object[] {BLOBF.getClass("net.minecraft.world.World", OBFLevel.MCP) }, beforeReturn);
        //Event fired separately to ensure players are fully setup
        addBLConstructorEvent(EventSide.SERVER, "net.minecraft.entity.player.EntityPlayer", new Object[] {BLOBF.getClass("net.minecraft.world.World", OBFLevel.MCP), GameProfile.class }, beforeReturn);
        
        addBLEvent(EventSide.VILLAGER, "net.minecraft.entity.passive.EntityVillager.onInitialSpawn (Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/entity/IEntityLivingData;)Lnet/minecraft/entity/IEntityLivingData;");
        addBLEvent(EventSide.VILLAGER, "net.minecraft.entity.passive.EntityVillager.setProfession (I)V");
        addBLEvent(EventSide.VILLAGER, "net.minecraft.entity.passive.EntityVillager.getProfession ()I");
        addBLEvent(EventSide.VILLAGER, "net.minecraft.entity.passive.EntityVillager.onGrowingAdult ()V");
        addBLEvent(EventSide.VILLAGER, "net.minecraft.entity.passive.EntityVillager.func_175553_cp ()Z");
        addBLEvent(EventSide.VILLAGER, "net.minecraft.entity.passive.EntityVillager.func_175555_cq ()Z");
        addBLEvent(EventSide.VILLAGER, "net.minecraft.entity.passive.EntityVillager.func_175557_cr ()Z");
        addBLEvent(EventSide.VILLAGER, "net.minecraft.entity.passive.EntityVillager.getDisplayName ()Lnet/minecraft/util/IChatComponent;");
        addBLEvent(EventSide.VILLAGER, "net.minecraft.entity.passive.EntityVillager.readEntityFromNBT (Lnet/minecraft/nbt/NBTTagCompound;)V");
        addBLEvent(EventSide.VILLAGER, "net.minecraft.entity.passive.EntityVillager.populateBuyingList ()V");
        addBLEvent(EventSide.VILLAGER, "net.minecraft.client.renderer.entity.RenderVillager.getEntityTexture (Lnet/minecraft/entity/passive/EntityVillager;)Lnet/minecraft/util/ResourceLocation;");
        
        
        
        InjectionPoint onEntityCollidedWithBlock = new BeforeInvoke(BLMethodInfo.create(BLOBF.getMethod("net.minecraft.block.Block.onEntityCollidedWithBlock (Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/entity/Entity;)V", OBFLevel.MCP)));
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.Entity.moveEntity (DDD)V", onEntityCollidedWithBlock);
    }
    
    protected void addBLAccessors() {
    	addBLAccessor("EntityVillager");
    }
    
    public String getSide() {
    	return "server";
    }
}
