package com.blazeloader.event.handlers.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererChestHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.IBlockAccess;

import com.blazeloader.api.block.ApiBlock;
import com.blazeloader.api.client.render.BlockRenderRegistry;
import com.blazeloader.api.entity.IMousePickHandler;
import com.blazeloader.api.item.ItemRegistry;
import com.blazeloader.api.particles.ParticlesRegister;
import com.blazeloader.event.handlers.EventHandler;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;

/**
 * Event handler for events that are not passed to mods, but rather to BL itself
 */
public class InternalEventHandlerClient {
	public static void eventDispatchKeypresses(EventInfo<Minecraft> event) {
		if (EventHandler.inventoryEventHandlers.size() > 0) {
			Minecraft mc = event.getSource();
			if (mc == null) return;
			if (mc.thePlayer != null && !mc.thePlayer.isSpectator()) {
				if (mc.currentScreen == null || mc.currentScreen.allowUserInput) {
					for (int i = 0; i < mc.gameSettings.keyBindsHotbar.length; i++) {
			            if (mc.gameSettings.keyBindsHotbar[i].isPressed()) {
		                    if (EventHandler.inventoryEventHandlers.all().onSlotSelectionChanged(mc.thePlayer, mc.thePlayer.inventory.getCurrentItem(), i)) {
		                    	KeyBinding.onTick(mc.gameSettings.keyBindsHotbar[i].getKeyCode());
		                    }
		                    break;
		                }
		            }
		        }
			}
		}
	}
	
    public static void eventRegisterVariantNames(EventInfo<ModelBakery> event) {
    	ItemRegistry.instance().insertItemVariantNames(event.getSource().variantNames);
    }
    
    public static void eventGetTexture(ReturnEventInfo<BlockModelShapes, TextureAtlasSprite> event, IBlockState state) {
    	Block block = state.getBlock();
        IBakedModel model = event.getSource().getModelForState(state);
        ModelManager manager = event.getSource().getModelManager();
        
        if (model == null || model == manager.getMissingModel()) {
        	String texture = BlockRenderRegistry.lookupTexture(block);
        	if (texture != null) {
        		event.setReturnValue(manager.getTextureMap().getAtlasSprite(texture));
        	}
        }
    }
    
    public static void eventRegisterVanillaParticles(EventInfo<EffectRenderer> event) {
    	EffectRenderer renderer = event.getSource();
    	renderer.particleTypes = ParticlesRegister.instance().init(renderer.particleTypes);
    }
    
    public static void eventMiddleClickMouse(EventInfo<Minecraft> event) {
    	Minecraft mc = event.getSource();
    	if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY) {
    		Entity entity = mc.objectMouseOver.entityHit;
    		if (entity instanceof IMousePickHandler) {
    			ItemStack stack = ((IMousePickHandler)entity).onPlayerMiddleClick(mc.thePlayer);
    			if (stack != null && stack.stackSize > 0) {
    				boolean creative = mc.thePlayer.capabilities.isCreativeMode;
    				InventoryPlayer inventory = mc.thePlayer.inventory;
    				inventory.setCurrentItem(stack.getItem(), stack.getMetadata(), stack.getItem().getHasSubtypes(), creative);
    				if (creative) {
    	                int change = mc.thePlayer.inventoryContainer.inventorySlots.size() - 9 + inventory.currentItem;
    	                mc.playerController.sendSlotPacket(inventory.getStackInSlot(inventory.currentItem), change);
    	            }
    				event.cancel();
    			}
    		}
    	}
    }
    
    public static void eventRenderByItem(EventInfo<TileEntityRendererChestHelper> event, ItemStack itemStack) {
    	if (BlockRenderRegistry.tryRenderTileEntity(itemStack)) {
    		event.cancel();
    	}
    }
    
    public static void eventRenderItem(EventInfo<RenderItem> event, ItemStack stack, IBakedModel model) {
    	Block block = ApiBlock.getBlockByItem(stack.getItem());
        if (BlockRenderRegistry.isTileEntityRendered(block)) {
        	GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            if (BlockRenderRegistry.doRenderTileEntity(block, stack)) {
            	event.cancel();
            }
            GlStateManager.popMatrix();
        }
    }
}