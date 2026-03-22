package com.bingoextra.gui;

import com.bingoextra.utils.BiomeRenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class BiomeCompassTransparentButton extends Button {

	public BiomeCompassTransparentButton(int x, int y, int width, int height, Component label, OnPress onPress) {
		super(x, y, width, height, label, onPress, DEFAULT_NARRATION);
	}

	@Override
	public void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			Minecraft mc = Minecraft.getInstance();
			int color = BiomeRenderUtils.getBackgroundColor(active, isHovered);

			guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);
			guiGraphics.drawCenteredString(mc.font, getMessage(), getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, 0xffffffff);
		}
	}

}
