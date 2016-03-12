package com.blazeloader.event.handlers.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blazeloader.api.block.ApiBlock;
import com.blazeloader.api.client.render.BlockRenderRegistry;
import com.blazeloader.api.entity.IMousePickHandler;
import com.blazeloader.event.handlers.EventHandler;
import com.blazeloader.event.mixin.Mix;

/**
 * Event handler for events that are not passed to mods, but rather to BL itself
 */
public class InternalEventHandlerClient {
	public static void eventDispatchKeypresses(Minecraft sender) {
		if (EventHandler.inventoryEventHandlers.size() > 0) {
			if (sender == null) return;
			if (sender.thePlayer != null && !sender.thePlayer.isSpectator()) {
				if (sender.currentScreen == null || sender.currentScreen.allowUserInput) {
					for (int i = 0; i < sender.gameSettings.keyBindsHotbar.length; i++) {
			            if (sender.gameSettings.keyBindsHotbar[i].isPressed()) {
		                    if (EventHandler.inventoryEventHandlers.all().onSlotSelectionChanged(sender.thePlayer, sender.thePlayer.inventory.getCurrentItem(), i)) {
		                    	KeyBinding.onTick(sender.gameSettings.keyBindsHotbar[i].getKeyCode());
		                    }
		                    break;
		                }
		            }
		        }
			}
		}
	}
	
    public static void eventGetTexture(BlockModelShapes sender, CallbackInfoReturnable<TextureAtlasSprite> info, IBlockState state) {
    	Block block = state.getBlock();
        IBakedModel model = sender.getModelForState(state);
        ModelManager manager = sender.getModelManager();
        if (model == null || model == manager.getMissingModel()) {
        	String texture = BlockRenderRegistry.lookupTexture(block);
        	if (texture != null) info.setReturnValue(manager.getTextureMap().getAtlasSprite(texture));
        }
    }
    
    public static void eventMiddleClickMouse(Minecraft sender, CallbackInfo info) {
    	if (sender.objectMouseOver != null && sender.objectMouseOver.typeOfHit == MovingObjectType.ENTITY) {
    		Entity entity = sender.objectMouseOver.entityHit;
    		if (entity instanceof IMousePickHandler) {
    			ItemStack stack = ((IMousePickHandler)entity).onPlayerMiddleClick(sender.thePlayer);
    			if (stack != null && stack.stackSize > 0) {
    				boolean creative = sender.thePlayer.capabilities.isCreativeMode;
    				InventoryPlayer inventory = sender.thePlayer.inventory;
    				inventory.setCurrentItem(stack.getItem(), stack.getMetadata(), stack.getItem().getHasSubtypes(), creative);
    				if (creative) {
    	                int change = sender.thePlayer.inventoryContainer.inventorySlots.size() - 9 + inventory.currentItem;
    	                sender.playerController.sendSlotPacket(inventory.getStackInSlot(inventory.currentItem), change);
    	            }
    				info.cancel();
    			}
    		}
    	}
    }
    
    public static void eventRenderByItem(CallbackInfo info, ItemStack itemStack) {
    	Mix.intercept(BlockRenderRegistry.tryRenderTileEntity(itemStack), info);
    }
    
    public static void eventRenderItem(CallbackInfo info, ItemStack stack, IBakedModel model) {
    	Block block = ApiBlock.getBlockByItem(stack.getItem());
        if (BlockRenderRegistry.isTileEntityRendered(block)) {
        	GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            Mix.intercept(BlockRenderRegistry.doRenderTileEntity(block, stack), info);
            GlStateManager.popMatrix();
        }
    }
}