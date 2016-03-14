package com.blazeloader.api.client;

import org.lwjgl.opengl.Display;

import net.minecraft.client.Minecraft;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * API functions for changing the client window.  Title branding, etc.
 */
public class ApiWindowClient {
    /**
     * Sets the window icon from a mod resource pack
     * <p/>
     * icons are loaded from the following location:
     * <i><br>assets/ {resource_pack_name} /icon/icon_16x16.png
     * <br>assets/ {resource_pack_name} /icon/icon_32x32.png</i>
     * <p/>
     * at least one of these images must be present
     *
     * @param resourcePack Name of resource pack
     */
    public static void setIcon(String resourcePack) {
        File icon16x = ResourceLoc.getResource(resourcePack, "icon/icon_16x16.png");
        File icon32x = ResourceLoc.getResource(resourcePack, "icon/icon_32x32.png");

        if (icon16x != null && icon32x == null) icon32x = icon16x;
        if (icon16x == null && icon32x != null) icon16x = icon32x;
        if (icon16x != null) {
            try {
                Display.setIcon(new ByteBuffer[]{getIcon(icon16x), getIcon(icon32x)});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the window title
     *
     * @param title The title to use for the Minecraft window
     */
    public static void setTitle(String title) {
        Display.setTitle(title);
    }
    
    /**
     * Gets the title used for the Minecraft window
     * 
     * @return title
     */
    public static String getTitle() {
    	return Display.getTitle();
    }
    
    private static ByteBuffer getIcon(File par1File) throws IOException {
        BufferedImage img = ImageIO.read(par1File);
        int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        ByteBuffer result = ByteBuffer.allocate(4 * pixels.length);
        for (int i : pixels) {
            result.putInt(i << 8 | i >> 24 & 255);
        }
        result.flip();
        return result;
    }
    
    /**
     * Sets the game's fullscreen mode
     * 
     */
    public static void setFullscreen(boolean fullscreen) {
    	Minecraft client = ApiClient.getClient();
    	if (client != null) {
    		if (client.isFullScreen() != fullscreen) {
    			client.toggleFullscreen();
    		}
    	}
    }
}
