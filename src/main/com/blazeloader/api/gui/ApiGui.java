package com.blazeloader.api.gui;

import com.blazeloader.util.JSArrayUtils;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.util.ChatComponentTranslation;

/**
 * GUI functions
 */
public class ApiGui {
	/**
	 * Opens a mod added container.
	 * 
	 * @param player      The player to open the container for
	 * @param inventory	  The tile entity providing the inventory
	 */
    public static void openContainer(EntityPlayerMP player, IModInventory inventory) {
        if (player.openContainer != player.inventoryContainer) player.closeScreen();
        
        if (inventory instanceof IModLockableInventory) {
            IModLockableInventory lockable = (IModLockableInventory)inventory;
            if (lockable.isLocked() && !player.canOpen(lockable.getLockCode()) && !player.isSpectator()) {
                player.playerNetServerHandler.sendPacket(new S02PacketChat(new ChatComponentTranslation(lockable.getLockMessageString(), inventory.getDisplayName()), (byte) 2));
                player.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(lockable.getLockSoundString(), player.posX, player.posY, player.posZ, 1.0F, 1.0F));
                return;
            }
        }
        
        player.getNextWindowId();
        Container container = inventory.createContainer(player.inventory, player);
        player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, inventory.getGuiID(), inventory.getDisplayName(), inventory.getSizeInventory()));
        player.openContainer = container;
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.onCraftGuiOpened(player);
    }
    
    /**
     * Registers an additional creative tab for a mod.
     * 
     * @param mod				Mod name
     * @param name				Name of creative tab
     * @param iconItem			Item to be displayed on the tab
     * @param iconMetadata		Metadata to be used with the item
     * @return	A CreativeTabs instance.
     */
    public static CreativeTabs registerCreativeTab(String mod, String name, Item iconItem, int iconMetadata) {
    	CreativeTabs.creativeTabArray = JSArrayUtils.push(CreativeTabs.creativeTabArray, (CreativeTabs)null);
    	return new CreativeTabs(CreativeTabs.creativeTabArray.length-1, mod + "." + name) {
			public Item getTabIconItem() {
				return iconItem;
			}
			public int getIconItemDamage() {
				return iconMetadata;
			}
			public int getTabColumn() {
		        return (getTabIndex() - 12) % 5;
		    }
			public boolean isTabInFirstRow() {
				return (getTabIndex() - 12) % 10 < 5;
			}
    	};
    }
    
    /**
     * Registers an additional creative tab for a mod.
     * 
     * @param mod				Mod name
     * @param name				Name of creative tab
     * @param stack				The itemstack to be displayed on the tab
     * @return	A CreativeTabs instance.
     */
    public static CreativeTabs registerCreativeTab(String mod, String name, ItemStack stack) {
    	return registerCreativeTab(mod, name, stack.getItem(), stack.getMetadata());
    }
}
