package com.blazeloader.api.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;

public class ModelPlane extends ModelUVMappedBox {
	
	private boolean[] sidesHidden = new boolean[6];
	
	public ModelPlane(ModelRenderer renderer, int texOffsetX, int texOffsetY, float originX, float originY, float originZ, int width, int height, int depth, float scale) {
    	this(renderer, texOffsetX, texOffsetY, originX, originY, originZ, width, height, depth, scale, renderer.mirror);
    }
    
    public ModelPlane(ModelRenderer renderer, int texOffsetX, int texOffsetY, float originX, float originY, float originZ, int width, int height, int depth, float scale, boolean mirror) {
    	super(renderer, texOffsetX, texOffsetY, originX, originY, originZ, width, height, depth, scale, mirror);
    }
    
    public ModelPlane setHiddenSides(boolean... sides) {
    	sidesHidden = sides;
    	return this;
    }
    
    public ModelPlane hideAllSides() {
    	setHiddenSides(true,true,true,true,true,true);
    	return this;
    }
    
    public ModelPlane setSideVisibility(int side, boolean visible) {
    	sidesHidden[side] = !visible;
    	return this;
    }
    
    public void render(WorldRenderer renderer, float scale) {
        for (int i = 0; i < this.quadList.length; i++) {
        	if (!sidesHidden[i]) {
        		quadList[i].draw(renderer, scale);
        	}
        }
    }
}
