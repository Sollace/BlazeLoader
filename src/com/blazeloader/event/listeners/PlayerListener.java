package com.blazeloader.event.listeners;

import com.blazeloader.bl.mod.BLMod;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.BlockPos;

/**
 * Server-side player events.
 */
public interface PlayerListener extends BLMod {

    /**
     * Called when a player attempts to log in. This is after the game has already checked if the user is valid.
     * <p>
     * Can be used to stop a player from connecting to this server.
     * 
     * @param args		Login arguments for this event. Includes the GameProfile and any error messages placed by other mod.
     */
    public void onPlayerTryLoginMP(LoginEventArgs args);


    /**
     * Called when a player logs into the game.
     *
     * @param player The player logging in.
     */
    public void onPlayerLoginMP(ServerConfigurationManager manager, EntityPlayerMP player);

    /**
     * Called when a player logs out of the game.
     *
     * @param player The player logging out.
     */
    public void onPlayerLogoutMP(ServerConfigurationManager manager, EntityPlayerMP player);


    /**
     * Called when a non-local player respawns.  Only works for other players.
     *
     * @param oldPlayer     The player that died.
     * @param dimension     The dimension to spawn in.
     * @param causedByDeath If the respawn was triggered by death, vs beating the game.
     */
    public void onPlayerRespawnMP(ServerConfigurationManager manager, EntityPlayerMP oldPlayer, int dimension, boolean causedByDeath);
    
    /**
     * Called when an entity collides with a player.
     * <p>
     * This can be used as a more flexible alternative to InventoryListener.onItemPickup
     * <br>
     * Because it occurs before pickup handling is called modders are provided the additional option to cancel item pickup and/or insert their own handling.
     * 
     * @param entity		The entity doing the work
     * @param player		The player the entity has collided with
     * @return true to allow the entity to collide, false to cancel the event.
     */
    public boolean onEntityCollideWithPlayer(Entity entity, EntityPlayer player);
    
    /**
     * Called when a player collides with a block.
     * 
     * @param state		Type of block being hit
     * @param pos		Location of block
     * @param player	The player
     */
    public void onPlayerCollideWithBlock(IBlockState state, BlockPos pos, EntityPlayer player);
    
    /**
     * Called when a player falls, but immediately before any action is taken.
     * <p>
     * Mods may add their own functionality here or cancel the even entirely.
     * 
     * @param player		The player this event applies to
     * @param arg			Extra data for the current event.
     */
    public void onPlayerFall(EntityPlayer player, FallEventArgs arg);
    
    /**
     * Event arguments for a login attempt.
     */
    public static class LoginEventArgs {
    	private GameProfile gameProfile;
    	private String error = null;
    	
    	private boolean blocked = false;
    	
    	public LoginEventArgs(GameProfile profile) {
    		gameProfile = profile;
    	}
    	
    	/**
    	 * The player has been rejected by an earlier mod.
    	 */
    	public boolean isBlocked() {
    		return blocked;
    	}
    	
    	/**
    	 * Rejects this player.
    	 * <p>
    	 * Once the event has left the current handler there is no way to undo this.
    	 * No other mods will recieve this event after that point.
    	 * 
    	 * @param errorMessage	The error message to be displayed on the client
    	 */
    	public void block(String errorMessage) {
    		error = errorMessage;
    		blocked = true;
    	}
    	
    	/**
    	 * Resets block states.
    	 * <p>
    	 * This serves as a fail safe for mods.
    	 */
    	public void unblock() {
    		blocked = false;
    		error = null;
    	}
    	
    	/**
    	 * The game profile for the player currently trying to login. 
    	 */
    	public GameProfile getGameProfile() {
    		return gameProfile;
    	}
    	
    	/**
    	 * Gets the message currently set to be sent to the client.
    	 */
    	public String getMessage() {
    		return error;
    	}
    }
    
    /**
     * Event arguments for falling entities
     */
    public static class FallEventArgs {
    	private float distance;
    	private float multiplier;
    	
    	private boolean canceled = false;
    	
    	/**
    	 * Returns true if the event must be discarded.
    	 * </br>
    	 * Other mods may still receive the event after {@code cancel} is called so you should always check this first when handling an event.
    	 */
    	public boolean isCancelled() {
    		return canceled;
    	}
    	
    	/**
    	 * Cancels this event. The player will not take any fall damage or playe the landing sound.
    	 */
    	public void cancel() {
    		canceled = true;
    	}
    	
    	public FallEventArgs(float dist, float mult) {
    		distance = dist;
    		multiplier = mult;
    	}
    	
    	/**
    	 * Gets the total distance this player has fallen
    	 */
    	public float getFallDistance() {
    		return distance;
    	}
    	
    	/**
    	 * Sets the total distance this player has fallen
    	 */
    	public void setFallDistance(float dist) {
    		distance = dist;
    	}
    	
    	/**
    	 * Sets the amount total damage this player will recieve per block fallen.
    	 */
    	public void setDamageMultiplier(float mult) {
    		multiplier = mult;
    	}
    	
    	/**
    	 * Gets the amount total damage this player will recieve per block fallen.
    	 */
    	public float getDamageMultiplier() {
    		return multiplier;
    	}
    }
}
