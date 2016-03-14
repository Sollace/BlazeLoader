package com.blazeloader.event.listeners;

import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.event.listeners.args.FallEventArgs;
import com.blazeloader.event.listeners.args.LoginEventArgs;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;

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
    public void onPlayerLoginMP(PlayerList manager, EntityPlayerMP player);

    /**
     * Called when a player logs out of the game.
     *
     * @param player The player logging out.
     */
    public void onPlayerLogoutMP(PlayerList manager, EntityPlayerMP player);


    /**
     * Called when a non-local player respawns.  Only works for other players.
     *
     * @param oldPlayer     The player that died.
     * @param dimension     The dimension to spawn in.
     * @param causedByDeath If the respawn was triggered by death, vs beating the game.
     */
    public void onPlayerRespawnMP(PlayerList manager, EntityPlayerMP oldPlayer, int dimension, boolean causedByDeath);
    
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
}
