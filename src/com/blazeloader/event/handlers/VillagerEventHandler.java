package com.blazeloader.event.handlers;

import com.blazeloader.api.entity.Professions;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;

import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;

public class VillagerEventHandler {
	
	private static boolean lockProfession = false;
	
	public static void eventOnInitialSpawn(ReturnEventInfo<EntityVillager, IEntityLivingData> event, DifficultyInstance difficulty, IEntityLivingData livingdata) {
		if (!lockProfession) {
			lockProfession = true;
			event.setReturnValue(event.getSource().onInitialSpawn(difficulty, livingdata));
			lockProfession = false;
		}
	}
	
	public static void eventSetProfession(EventInfo<EntityVillager> event, int professionId) {
		if (lockProfession) {
			lockProfession = false;
			event.cancel();
			event.getSource().setProfession(Professions.instance().randomProfessionId(event.getSource().worldObj.rand));
		}
	}
	
	public static void eventGetProfession(ReturnEventInfo<EntityVillager, Integer> event) {
        event.setReturnValue(Math.max(event.getSource().getDataWatcher().getWatchableObjectInt(16), 0));
    }
	
	public static void eventPopulateBuyingList(EventInfo<EntityVillager> event) {
		if (Professions.instance().populateBuyingList(event.getSource())) {
			event.cancel();
		}
	}
	
	public static void eventOnGrowingAdult(EventInfo<EntityVillager> event) {
		if (Professions.instance().onGrownUp(event.getSource())) {
			event.cancel();
		}
	}
	
    public static void eventFunc_175553_cp(ReturnEventInfo<EntityVillager, Boolean> event) {
    	Boolean result = Professions.instance().checkMatingConditions(event.getSource(), true, 1);
        if (result != null) {
        	event.setReturnValue(result);
        }
    }

    public static void eventFunc_175555_cq(ReturnEventInfo<EntityVillager, Boolean> event) {
    	Boolean result = Professions.instance().checkMatingConditions(event.getSource(), true, 2);
        if (result != null) {
        	event.setReturnValue(result);
        }
    }

    public static void eventFunc_175557_cr(ReturnEventInfo<EntityVillager, Boolean> event) {
    	Boolean result = Professions.instance().checkMatingConditions(event.getSource(), false, 1);
        if (result != null) {
        	event.setReturnValue(result);
        }
    }
    
    public static void eventGetDisplayName(ReturnEventInfo<EntityVillager, IChatComponent> event) {
    	IChatComponent result = Professions.instance().getDisplayName(event.getSource());
    	if (result != null) {
    		event.setReturnValue(result);
    	}
    }
    
    public static void eventReadEntityFromNBT(EventInfo<EntityVillager> event, NBTTagCompound tagCompund) {
    	Professions.instance().setAdditionalAITasks(event.getSource());
    }
    
    public static void eventGetEntityTexture(ReturnEventInfo<RenderVillager, ResourceLocation> event, EntityVillager villager) {
		ResourceLocation result = Professions.instance().getResourceLocation(villager);
		if (result != null) {
			event.setReturnValue(result);
		}
    }
}
