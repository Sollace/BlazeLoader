package com.blazeloader.util.playerinfo;

import java.util.UUID;

import com.blazeloader.util.data.INBTWritable;
import com.blazeloader.util.version.Versions;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PreYggdrasilConverter;

/**
 * Combined identity for a player. Includes id, name, skins, etc.
 */
public class PlayerIdent implements INBTWritable {
	private GameProfile gameProfile;
	private UUID uuid;
	
	private PlayerInfoProvider playerInfoP;
	
	public static PlayerIdent create() {
		if (Versions.isClient()) {
			return new PlayerIdentClient();
		}
		return new PlayerIdent();
	}
	
	protected PlayerIdent() {}
	
	/**
	 * Loads an existing PlayerIdent from nbt storage.
	 */
	public static PlayerIdent create(NBTTagCompound tag) {
		PlayerIdent result = create();
		result.readFromNBT(tag);
		return result;
	}
	
	/**
	 * Creates a new PlayerIdent backed with the same details as the given player.
	 * 
	 * @param player	A player
	 */
	public static PlayerIdent create(EntityPlayer player) {
		return create().construct(player.getGameProfile());
	}
	
	/**
	 * Creates a new PlayerIdent with details for the given username.
	 * <br>
	 * A user id will be computed from the given name.
	 * 
	 * @param username	A minecraft player's username
	 */
	public static PlayerIdent create(String username) {
		return create(lookupUUID(username), username);
	}
	
	/**
	 * Creates a new PlayerIdent with details for the given user id and name.
	 * 
	 * @param uuid		User id
	 * @param username	User name
	 */
	public static PlayerIdent create(UUID uuid, String username) {
		return create().construct(new GameProfile(uuid, username));
	}
	
	private PlayerIdent construct(GameProfile profile) {
		gameProfile = profile;
		return this;
	}
	
	private static UUID lookupUUID(String username) {
		return UUID.fromString(PreYggdrasilConverter.func_152719_a(username));
	}
	
	/**
	 * Gets this player's unique id associated with their account.
	 */
	public UUID getUniqueID() {
		if (uuid == null) {
			uuid = EntityPlayer.getUUID(getGameProfile());
		}
		return uuid;
	}
	
	/**
	 * Gets this player's game profile.
	 */
	public GameProfile getGameProfile() {
		return gameProfile;
	}
	
	/**
	 * Checks if there is a player info object.
	 * <br>
	 * Returns false on the server.
	 */
    public boolean hasPlayerInfo() {
        return getPlayerInfo() != null;
    }
	
    /**
     * Gets the player info object responsible for fetching player skins.
     * <br>
     * Returns null on the server.
     */
	public NetworkPlayerInfo getPlayerInfo() {
		return null;
	}
	
	protected PlayerInfoProvider getProvider() {
		if (playerInfoP == null) playerInfoP = new PlayerInfoProvider(getUniqueID());
		return playerInfoP;
	}
	
	/**
	 * Checks if this player has a skin defined for their profile.
	 */
	public boolean hasSkin() {
		return getProvider().hasSkin();
	}
	
	/**
	 * Checks if this player has a cape for their profile.
	 */
	public boolean hasCape() {
		return getProvider().hasCape();
	}
    
	public void writeToNBT(NBTTagCompound tagCompound) {
		tagCompound.setLong("UUIDMost", getUniqueID().getMostSignificantBits());
		tagCompound.setLong("UUIDLeast", getUniqueID().getLeastSignificantBits());
		tagCompound.setString("Name", getGameProfile().getName());
	}
	
	public void readFromNBT(NBTTagCompound tagCompound) {
		uuid = new UUID(tagCompound.getLong("UUIDMost"), tagCompound.getLong("UUIDLeast"));
		gameProfile = new GameProfile(uuid, tagCompound.getString("Name"));
	}
}
