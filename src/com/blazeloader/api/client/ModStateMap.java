package com.blazeloader.api.client;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.blazeloader.api.block.ApiBlock;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class ModStateMap extends StateMapperBase {
	private final String modid;
    private final IProperty mappedProperty;
    private final String suffix;
    private final List<IProperty> ignoredProperties;
    
    private ModStateMap(String modid, IProperty mappedProperty, String suffix, List<IProperty> ignoredProperties) {
    	this.modid = modid;
        this.mappedProperty = mappedProperty;
        this.suffix = suffix;
        this.ignoredProperties = ignoredProperties;
    }

    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        LinkedHashMap<IProperty, Comparable> stateProperties = Maps.newLinkedHashMap(state.getProperties());
        String modelLocation;

        if (mappedProperty == null) {
            modelLocation = ApiBlock.getStringBlockName(state.getBlock());
        } else {
            modelLocation = modid + ":" + mappedProperty.getName(stateProperties.remove(mappedProperty));
        }

        if (suffix != null) modelLocation += suffix;
        
        for (IProperty i : ignoredProperties) {
            stateProperties.remove(i);
        }

        return new ModelResourceLocation(modelLocation, getPropertyString(stateProperties));
    }

    public static class Builder {
        private IProperty property;
        private String suffix;
        private String modId;
        private final List excludedProperties = Lists.newArrayList();
        
        public Builder setProperty(IProperty mappedProperty) {
            property = mappedProperty;
            return this;
        }

        public Builder setSuffix(String suffex) {
            suffix = suffex;
            return this;
        }
        
        public Builder setModId(String mod) {
        	modId = mod;
        	return this;
        }
        
        public Builder addPropertiesToIgnore(IProperty ... ignored) {
            Collections.addAll(excludedProperties, ignored);
            return this;
        }
        
        public ModStateMap build() {
            return new ModStateMap(modId, property, suffix, excludedProperties);
        }
    }
}
