package com.blazeloader.util.playerinfo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;

import com.blazeloader.api.client.ApiClient;
import com.blazeloader.bl.main.BLMain;
import com.google.common.base.Objects;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Unchecked downloading of player skins and capes. Because the vanilla skin manager doesn't always work.
 */
public class SkinProvider {
	private File skincache;
	
	private ResourceLocation locationSkin;
	private ResourceLocation locationCape;
	
	private PlayerInfoProvider playerInfo;
	
	public SkinProvider(GameProfile profile) {
		this(EntityPlayer.getUUID(profile));
	}
	
	public SkinProvider(UUID uuid) {
		this(new PlayerInfoProvider(uuid));
	}
	
	public SkinProvider(PlayerInfoProvider provider) {
		playerInfo = provider;
		skincache = new File(BLMain.instance().environment.getAssetsDirectory(), "skins");
	}
	
	public boolean hasSkin() {
        return locationSkin != null;
    }
	
	public boolean hasCape() {
		return provideCape() != null;
	}
	
	public String getSkinType() {
		return playerInfo.skinType == null ? DefaultPlayerSkin.getSkinType(playerInfo.uuid) : playerInfo.skinType;
	}
	
	public ResourceLocation provideSkin() {
		if (locationSkin == null) {
			if (!playerInfo.hasLoaded) playerInfo.loadPlayerInfo();
			if (playerInfo.urlSkin != null) {
	    		getDownloadImageSkin(playerInfo.urlSkin);
	    		playerInfo.urlSkin = null;
	    	}
    	}
    	return Objects.firstNonNull(locationSkin, DefaultPlayerSkin.getDefaultSkin(playerInfo.uuid));
	}
	
	public ResourceLocation provideCape() {
		if (locationCape == null) {
    		if (!playerInfo.hasLoaded) playerInfo.loadPlayerInfo();
    		if (playerInfo.urlCape != null) {
        		getDownloadImageCape(playerInfo.urlCape);
        		playerInfo.urlCape = null;
        	}
    	}
    	return locationCape;
	}
	
	private File getCacheLocation(String hash) {
    	File cacheFolder = new File(skincache, hash.substring(0, 2));
        return new File(cacheFolder, hash);
    }
    
    private String getHash(String url) {
    	String[] split = url.split("/|\\\\");
    	return split[split.length - 1];
    }
	
    private void getDownloadImageSkin(String url) {
    	String hash = getHash(url);
        ResourceLocation loc = new ResourceLocation("skins/" + hash);
        if (ApiClient.getTextureManager().getTexture(loc) != null) {
        	locationSkin = loc;
        	return;
        }
        ITextureObject texture = new ThreadDownloadImageData(getCacheLocation(hash), url, DefaultPlayerSkin.getDefaultSkinLegacy(), new ImageBufferDownload() {
        	public void skinAvailable() {
        		super.skinAvailable();
        		locationSkin = loc;
        	}
        });
        ApiClient.getTextureManager().loadTexture(loc, texture);
    }
    
    private void getDownloadImageCape(String url) {
    	String hash = getHash(url);
        ResourceLocation loc = new ResourceLocation("skins/" + hash);
        if (ApiClient.getTextureManager().getTexture(loc) != null) {
        	locationSkin = loc;
        	return;
        }
        ITextureObject texture = new ThreadDownloadImageData(getCacheLocation(hash), url, DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer() {
            public BufferedImage parseUserSkin(BufferedImage image) {
                return image;
            }
            public void skinAvailable() {
                locationCape = loc;
            }
        });
        ApiClient.getTextureManager().loadTexture(loc, texture);
    }
}
