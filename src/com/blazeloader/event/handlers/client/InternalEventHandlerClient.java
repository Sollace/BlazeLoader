package com.blazeloader.event.handlers.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.KeyBinding;

import com.blazeloader.api.client.render.BlockRenderRegistry;
import com.blazeloader.api.item.ItemRegistry;
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
        		event.cancel();
        	}
        }
    }
}