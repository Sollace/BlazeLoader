package com.blazeloader.util.time;

import net.minecraft.world.World;

/**
 * A clock bound to the current in-game time.
 * 
 */
public class WorldClock implements IPreciseTime, IDate {
	
	private final World world;
	
	public WorldClock(World referenceFrame) {
		world = referenceFrame;
	}
	
	public String getDate() {
		return getDay().ordinal() + "-" + getMonth().ordinal() + "-" + getYears();
	}
	
	public int getDays() {
		return getTotalDays() - getWeeks()*7;
	}
	
	public int getWeeks() {
		return getTotalWeeks() - getMonths()*4;
	}
	
	public int getMonths() {
		return getTotalMonths() - getYears()*12;
	}
	
	public int getYears() {
		return (int)Math.floor(getTotalMonths()/12);
	}
	
	public Day getDay() {
		return Day.values()[getDays() % 7];
	}
	
	public Month getMonth() {
		return Month.values()[getMonths() % 12];
	}
	
	/**
	 * Minecraft does not have leap years. :P
	 */
	public boolean isLeap() {
		return false;
	}
	
	/**
	 * AN = After Notch.
	 * <p>
	 * There is only one era.
	 */
	public String getEra() {
		return "AN";
	}
	
	public String getTime() {
		return getHours() + ":" + getMinutes() + ":" + getSeconds();
	}
	
	public int getHours() {
		return getTotalHours() - getDays()*24;
	}
	
	public int getMinutes() {
		return getTotalMinutes() - getHours()*60;
	}
	
	public int getSeconds() {
		return getTotalSeconds() - getMinutes()*60;
	}
	
	public String getTimePrecise() {
		return getTime() + ":" + getMilliseconds();
	}
	
	public int getMilliseconds() {
		return (int)Math.floor(getTotalMilliseconds()) - getSeconds()*1000;
	}
	
	public long getTotalMilliseconds() {
		return world.getTotalWorldTime();
	}
	
	private int getTotalSeconds() {
		return (int)Math.floor((double)getTotalMilliseconds() / 1000);
	}
	
	private int getTotalMinutes() {
		return (int)Math.floor(getTotalSeconds() / 60);
	}
	
	private int getTotalHours() {
		return (int)Math.floor(getTotalMinutes() / 60);
	}
	
	private int getTotalDays() {
		return (int)Math.floor(getTotalHours() / 24);
	}
	
	private int getTotalWeeks() {
		return (int)Math.floor(getTotalDays() / 7);
	}
	
	private int getTotalMonths() {
		return (int)Math.floor(getTotalWeeks() / 4);
	}
}
