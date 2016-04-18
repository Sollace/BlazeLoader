package com.blazeloader.event.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import com.blazeloader.api.privileged.ICreativeMenuForge;

import net.minecraft.client.gui.inventory.GuiContainerCreative;

@Mixin(GuiContainerCreative.class)
public class MGuiContainerCreative implements ICreativeMenuForge {
	@SuppressWarnings("unused")
	private static int tabPage;
	@SuppressWarnings("unused")
	private int maxPages;
	
	public void setPages(int pages) {
		tabPage = maxPages = pages;
	}
}
