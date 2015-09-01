package com.blazeloader.api.privileged;

import com.blazeloader.bl.obf.BLOBF;
import com.blazeloader.bl.obf.OBFLevel;

public abstract class Privileged extends BLOBF {
	private Privileged() {super("","","");}
	
	protected static final class EntityVillager extends Privileged {
		public static final BLOBF
			clazz = BLOBF.getClass("net.minecraft.entity.passive.EntityVillager", OBFLevel.MCP),
			areAdditionalTasksSet = BLOBF.getField("net.minecraft.entity.passive.EntityVillager.areAdditionalTasksSet", OBFLevel.MCP),
			careerLevel = BLOBF.getField("net.minecraft.entity.passive.EntityVillager.careerLevel", OBFLevel.MCP),
			careerId = BLOBF.getField("net.minecraft.entity.passive.EntityVillager.careerId", OBFLevel.MCP),
			buyingList = BLOBF.getField("net.minecraft.entity.passive.EntityVillager.buyingList", OBFLevel.MCP);
	}
}
