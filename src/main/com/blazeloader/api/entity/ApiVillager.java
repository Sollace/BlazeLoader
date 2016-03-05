package com.blazeloader.api.entity;

import com.blazeloader.api.entity.profession.IProfession;
import com.blazeloader.api.entity.profession.Professions;

public class ApiVillager {
	
	/**
	 * Registers a custom villager profession
	 */
	public static void registerProfession(IProfession profession) {
		Professions.instance().add(profession);
	}
}
