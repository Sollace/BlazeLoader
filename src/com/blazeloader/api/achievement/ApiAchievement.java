package com.blazeloader.api.achievement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatisticsFile;

public class ApiAchievement {
    /**
     * Unlocks an achievement for the given player.
     *
     * @param player      Player who has achieved
     * @param achievement What the player has achieved
     */
    public static void unlockAchievement(EntityPlayer player, Achievement achievement) {
        player.triggerAchievement(achievement);
    }
    
    /**
     * The opposite of unlock achievement. If the player has previously unlocked the achievement will reset it to its initial state.
     * 
     * @param player		The player who has achieved
     * @param achievement	What the player has achieved
     */
    public static void lockAchievement(EntityPlayerMP player, Achievement achievement) {
		StatisticsFile stats = player.getStatFile();
		if (stats.hasAchievementUnlocked(achievement)) {
				//setStatValue
			stats.func_150873_a(player, achievement, 0);
			    //sendStatUpdate
			stats.func_150876_a(player);
		}
    }
    
    /**
     * Checks if the player has unlocked the given achievement.
     * 
     * @param player		The player to check
     * @param achievement	The achievement to check for
     * @return	True if the player has that achievement, false otherwise.
     */
    public static boolean hasAchievementUnlocked(EntityPlayerMP player, Achievement achievement) {
		return player.getStatFile().hasAchievementUnlocked(achievement);
    }

    /**
     * Creates and registers a new achievement
     *
     * @param unlocalisedName The name for this achievement. Used for its id and chat message
     * @param gridX           X coordinate on the achievement screen
     * @param gridY           Y coordinate on the achievement screen
     * @param displayedItem   Item to display next to this achievement
     * @return New achievement ready to use
     */
    public static Achievement registerAchievement(String unlocalisedName, int gridX, int gridY, ItemStack displayedItem) {
        return registerAchievement(new Achievement(unlocalisedName, unlocalisedName, gridX, gridY, displayedItem, null)).setIndependent();
    }

    /**
     * Creates and registers a new achievement
     *
     * @param unlocalisedName     The name for this achievement. Used for its id and chat message
     * @param gridX               X coordinate on the achievement screen
     * @param gridY               Y coordinate on the achievement screen
     * @param displayedItem       Item to display next to this achievement
     * @param requiredAchievement Achievement that must be unlocked before this one
     * @return New achievement ready to use
     */
    public static Achievement registerAchievement(String unlocalisedName, int gridX, int gridY, ItemStack displayedItem, Achievement requiredAchievement) {
        return registerAchievement(new Achievement(unlocalisedName, unlocalisedName, gridX, gridY, displayedItem, requiredAchievement));
    }

    /**
     * Registers an achievement that has already been initialised
     *
     * @return New achievement ready to use
     */
    public static Achievement registerAchievement(Achievement achievement) {
        return achievement.registerAchievement();
    }
}
