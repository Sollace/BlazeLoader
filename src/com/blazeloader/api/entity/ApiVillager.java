package com.blazeloader.api.entity;

public class ApiVillager {
	
	/**
	 * Registers a custom villager profession
	 */
	public static void registerProfession(IProfession profession) {
		Professions.instance().add(profession);
	}
}
