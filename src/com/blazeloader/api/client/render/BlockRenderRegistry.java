package com.blazeloader.api.client.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;

public class BlockRenderRegistry {
	protected static final Map<Block, String> specialRenderBlocks = new HashMap<Block, String>();
	
	public static String lookupTexture(Block block) {
		if (specialRenderBlocks.containsKey(block)) {
			return specialRenderBlocks.get(block);
		}
		return null;
	}
}
