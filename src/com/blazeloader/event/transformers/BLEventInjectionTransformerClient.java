package com.blazeloader.event.transformers;

/**
 * Injects events into MC classes
 */
@Deprecated
public class BLEventInjectionTransformerClient extends BLEventInjectionTransformer {

    /**
     * Subclasses should register events here
     */
    @Override
    protected void addBLEvents() {
        //addBLEvent(EventSide.CLIENT, "net.minecraft.client.Minecraft.loadWorld (Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V");
        //addBLEvent(EventSide.CLIENT, "net.minecraft.client.Minecraft.displayGuiScreen (Lnet/minecraft/client/gui/GuiScreen;)V");
        //addBLEvent(EventSide.INTERNAL_CLIENT, "net.minecraft.client.Minecraft.dispatchKeypresses ()V", beforeReturn);
        //addBLEvent(EventSide.INTERNAL_CLIENT, "net.minecraft.client.Minecraft.middleClickMouse ()V");
        
        addBLEvent(EventSide.CLIENT, "net.minecraft.client.network.NetHandlerPlayClient.handleOpenWindow (Lnet/minecraft/network/play/server/S2DPacketOpenWindow;)V");
        addBLEvent(EventSide.CLIENT, "net.minecraft.client.network.NetHandlerPlayClient.handleHeldItemChange (Lnet/minecraft/network/play/server/S09PacketHeldItemChange;)V");
        addBLEvent(EventSide.CLIENT, "net.minecraft.client.network.NetHandlerPlayClient.handleCollectItem (Lnet/minecraft/network/play/server/S0DPacketCollectItem;)V");
        
        addBLEvent(EventSide.CLIENT, "net.minecraft.client.particle.EffectRenderer.spawnEffectParticle (IDDDDDD[I)Lnet/minecraft/client/particle/EntityFX;");
        addBLEvent(EventSide.INTERNAL_CLIENT, "net.minecraft.client.particle.EffectRenderer.registerVanillaParticles ()V", beforeReturn);
        
        addBLEvent(EventSide.CLIENT, "net.minecraft.client.entity.EntityPlayerSP.setPlayerSPHealth (F)V", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.client.ClientBrandRetriever.getClientModName ()Ljava/lang/String;", beforeReturn);
        addBLEvent(EventSide.INTERNAL_CLIENT, "net.minecraft.client.resources.model.ModelBakery.registerVariantNames ()V", beforeReturn);
        
        addBLEvent(EventSide.INTERNAL_CLIENT, "net.minecraft.client.renderer.BlockModelShapes.getTexture (Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;");
        addBLEvent(EventSide.INTERNAL_CLIENT, "net.minecraft.client.renderer.entity.RenderItem.renderItem (Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resources/model/IBakedModel;)V");
        addBLEvent(EventSide.INTERNAL_CLIENT, "net.minecraft.client.renderer.tileentity.TileEntityRendererChestHelper.renderByItem (Lnet/minecraft/item/ItemStack;)V");
        
        addBLEvent(EventSide.VILLAGER, "net.minecraft.client.renderer.entity.RenderVillager.getEntityTexture (Lnet/minecraft/entity/passive/EntityVillager;)Lnet/minecraft/util/ResourceLocation;");
    }
    
    @Override
    public String getSide() {
    	return "client";
    }
}

