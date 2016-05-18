package com.blazeloader.util.playerinfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;

/*
 * Client-side implementations
 */
class PlayerIdentClient extends PlayerIdent {
	private NetworkPlayerInfo playerInfo;
	
	public boolean hasPlayerInfo() {
        return true;
    }
	
	public NetworkPlayerInfo getPlayerInfo() {
		if (playerInfo == null) {
            playerInfo = new NetPlayerInfo(getGameProfile());
        }
        return playerInfo;
	}
	
    private final class NetPlayerInfo extends NetworkPlayerInfo {
    	private final SkinProvider skins;
    	
    	private NetPlayerInfo(GameProfile profile) {
    		super(profile);
    		skins = new SkinProvider(getProvider());
    	}
    	
    	public boolean hasLocationSkin() {
            return skins.hasSkin();
        }
    	
    	public String getSkinType() {
            return skins.getSkinType();
        }
    	
        public ResourceLocation getLocationSkin() {
        	super.getLocationSkin();
        	return skins.provideSkin();
        }
        
        public ResourceLocation getLocationCape() {
        	super.getLocationCape();
        	return skins.provideCape();
        }
        
        public ResourceLocation getLocationElytra() {
        	super.getLocationElytra();
        	return skins.provideElytra();
        }
    }
}
