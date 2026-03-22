package com.bingoextra.gui;

import com.bingoextra.utils.StructureCompassRenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class StructureCompassTransparentEditBox extends EditBox {

    public StructureCompassTransparentEditBox(Font font, int x, int y, int width, int height, Component label) {
        super(font, x, y, width, height, label);
    }

    @Override
    public int getInnerWidth() {
        return getWidth() - 8;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (isVisible()) {
            int fillColor = StructureCompassRenderUtils.getBackgroundColor(isActive(), false);
            guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), fillColor);
        }

        // Disable bordered to skip rendering the default background
        bordered = false;
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
        bordered = true;
    }

}