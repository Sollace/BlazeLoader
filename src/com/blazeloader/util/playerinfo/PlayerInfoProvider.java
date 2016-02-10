package com.blazeloader.util.playerinfo;

import java.util.UUID;

import com.blazeloader.util.config.JsonUtils;
import com.blazeloader.util.http.Downloader;
import com.blazeloader.util.http.JsonDownload;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class PlayerInfoProvider {
	
	protected UUID uuid;
	
	protected String urlSkin;
	protected String urlCape;
	
	protected String skinType;
	
	protected boolean hasSkin;
	protected boolean hasCape;
	
	protected boolean hasLoaded = false;
	
	protected PlayerInfoProvider(UUID uuid) {
		this.uuid = uuid;
	}
	
	public boolean hasSkin() {
		if (!hasLoaded) loadPlayerInfo();
		return hasSkin;
	}
	
	public boolean hasCape() {
		if (!hasLoaded) loadPlayerInfo();
		return hasCape;
	}
	
    protected void loadPlayerInfo() {
    	if (hasLoaded) return;
    	hasLoaded = true;
    	(new Downloader("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", ""))).download(new JsonDownload() {
			public void success(JsonElement json) {
				JsonObject obj = json.getAsJsonObject();
				if (obj.has("properties")) {
	            	obj = obj.getAsJsonArray("properties").get(0).getAsJsonObject();
	            	if (obj.has("value")) {
	            		String s = Downloader.decodeBas64(obj.get("value").getAsString());
	            		obj = JsonUtils.parseJSONObj(s);
	            		if (obj.has("textures")) {
	            			obj = obj.getAsJsonObject("textures");
	            			if (obj.has("SKIN")) {
	            				JsonObject skin = obj.getAsJsonObject("SKIN");
	            				urlSkin = skin.get("url").getAsString();
	            				hasSkin = true;
	            				if (skin.has("metadata")) {
	            					skin = skin.getAsJsonObject("metadata");
	            					skinType = skin.get("model").getAsString();
	            				} else {
	            					skinType = "default";
	            				}
	            			}
	            			if (obj.has("CAPE")) {
	            				urlCape = obj.getAsJsonObject("CAPE").get("url").getAsString();
	            				hasCape = true;
	            			}
	            		}
	            	}
	            }
			}
    	});
    }
}
