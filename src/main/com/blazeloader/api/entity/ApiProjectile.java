package com.blazeloader.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityThrowable;

public class ApiProjectile {
	
	/**
	 * Checks if the given entity is a projectile.
	 */
	public static boolean isProjectile(Entity e) {
		return e instanceof IProjectile || e instanceof EntityFireball || e instanceof EntityFishHook;
	}
	
	/**
	 * Checks if an entity is a thrown projectile.
	 */
	public static boolean isThrowable(Entity e) {
		return e instanceof EntityThrowable || e instanceof EntityArrow || e instanceof EntityFireball || e instanceof EntityFishHook; 
	}
	
	/**
	 * Checks if the given projectile was thrown by the given entity
	 */
	public static boolean isProjectileThrownBy(Entity throwable, Entity e) {
		return e != null && e.equals(getThrowingEntity(throwable));
	}
	
	/**
	 * Gets the thrower for a projectile or null
	 */
	public static Entity getThrowingEntity(Entity throwable) {
		if (throwable instanceof EntityThrowable) return ((EntityThrowable)throwable).getThrower();
		if (throwable instanceof EntityArrow) return ((EntityArrow)throwable).shootingEntity;
		if (throwable instanceof EntityFireball) return ((EntityFireball)throwable).shootingEntity;
		if (throwable instanceof EntityFishHook) return ((EntityFishHook)throwable).angler;
		return null;
	}
	
	/**
	 * Sets the thrower for a projectile.
	 */
	public static void setThrowingEntity(Entity throwable, Entity thrower) {
		if (throwable instanceof EntityThrowable) {
			((EntityThrowable)throwable).throwerName = null;
			((EntityThrowable)throwable).thrower = (EntityLivingBase)thrower;
		} else if (throwable instanceof EntityArrow) {
			((EntityArrow)throwable).shootingEntity = thrower;
		} else if (throwable instanceof EntityFireball) {
			((EntityFireball)throwable).shootingEntity = (EntityLivingBase)thrower;
		} else if (throwable instanceof EntityFishHook) {
			((EntityFishHook)throwable).angler = (EntityPlayer)thrower;
		}
	}
	
	/**
	 * Sets the velocity and heading for a projectile.
	 * 
	 * @param throwable		The projectile
	 * @param x				X Direction component
	 * @param y				Y Direction component
	 * @param z				Z Direction component
	 * @param velocity		Velocity
	 * @param inaccuracy	Inaccuracy
	 * @return				True the projectile's heading was set, false otherwise
	 */
	public static boolean setThrowableHeading(Entity throwable, double x, double y, double z, float velocity, float inaccuracy) {
		if (throwable instanceof IProjectile) {
			((IProjectile)throwable).setThrowableHeading(x, y, z, velocity, inaccuracy);
			return true;
		}
		if (throwable instanceof EntityFishHook) {
			((EntityFishHook)throwable).handleHookCasting(x, y, z, velocity, inaccuracy);
			return true;
		}
		velocity = (float)Math.sqrt(velocity);
		x *= velocity;
		y *= velocity;
		z *= velocity;
		if (throwable instanceof EntityFireball) {
			((EntityFireball)throwable).accelerationX = x;
			((EntityFireball)throwable).accelerationY = y;
			((EntityFireball)throwable).accelerationZ = z;
			return true;
		}
		throwable.setVelocity(x, y, z);
		return false;
	}
}
