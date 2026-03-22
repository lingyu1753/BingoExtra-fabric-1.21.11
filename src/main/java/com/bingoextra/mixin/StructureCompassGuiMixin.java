package com.bingoextra.mixin;

import com.bingoextra.config.StructureCompassConfig;
import com.bingoextra.core.component.ModDataComponents;
import com.bingoextra.item.ModItems;
import com.bingoextra.item.StructureCompassItem;
import com.bingoextra.utils.StructureCompassItemUtils;
import com.bingoextra.utils.StructureCompassRenderUtils;
import com.bingoextra.utils.StructureCompassState;
import com.bingoextra.utils.StructureUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public class StructureCompassGuiMixin {
	
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V", at = @At(value = "TAIL"))
	private void renderCompassInfo(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
		if (minecraft.player != null && !minecraft.options.hideGui && !minecraft.getDebugOverlay().showDebugScreen() && (minecraft.screen == null || (StructureCompassConfig.displayWithChatOpen && minecraft.screen instanceof ChatScreen))) {
			final Player player = minecraft.player;
			final ItemStack stack = StructureCompassItemUtils.getHeldItem(player, ModItems.STRUCTURE_COMPASS);
			if (stack != null && stack.getItem() instanceof StructureCompassItem) {
				final StructureCompassItem compass = (StructureCompassItem) stack.getItem();
				if (compass.getCompassState(stack) == StructureCompassState.SEARCHING) {
					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.status"), 5, 5, 0xffffffff, 0);
					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.searching"), 5, 5, 0xffaaaaaa, 1);

					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.structure"), 5, 5, 0xffffffff, 3);
					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, StructureUtils.getStructureName(Identifier.parse(stack.getOrDefault(ModDataComponents.STRUCTURE_ID, ""))), 5, 5, 0xffaaaaaa, 4);

					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.radius"), 5, 5, 0xffffffff, 6);
 					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, String.valueOf(stack.getOrDefault(ModDataComponents.SEARCH_RADIUS, 0)), 5, 5, 0xffaaaaaa, 7);
				} else if (compass.getCompassState(stack) == StructureCompassState.FOUND) {
					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.status"), 5, 5, 0xffffffff, 0);
					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.found"), 5, 5, 0xffaaaaaa, 1);

					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.structure"), 5, 5, 0xffffffff, 3);
					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, StructureUtils.getStructureName(Identifier.parse(stack.getOrDefault(ModDataComponents.STRUCTURE_ID, ""))), 5, 5, 0xffaaaaaa, 4);

					if (stack.getOrDefault(ModDataComponents.DISPLAY_COORDS, false)) {
						StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.coordinates"), 5, 5, 0xffffffff, 6);
						StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, stack.getOrDefault(ModDataComponents.FOUND_X, 0) + ", " + stack.getOrDefault(ModDataComponents.FOUND_Z, 0), 5, 5, 0xffaaaaaa, 7);

						StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.distance"), 5, 5, 0xffffffff, 9);
						StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, String.valueOf(StructureUtils.getHorizontalDistanceToLocation(player, stack.getOrDefault(ModDataComponents.FOUND_X, 0), stack.getOrDefault(ModDataComponents.FOUND_Z, 0))), 5, 5, 0xffaaaaaa, 10);
					}
				} else if (compass.getCompassState(stack) == StructureCompassState.NOT_FOUND) {
					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.status"), 5, 5, 0xffffffff, 0);
					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.notFound"), 5, 5, 0xffaaaaaa, 1);

					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.structure"), 5, 5, 0xffffffff, 3);
					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, StructureUtils.getStructureName(Identifier.parse(stack.getOrDefault(ModDataComponents.STRUCTURE_ID, ""))), 5, 5, 0xffaaaaaa, 4);

					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.radius"), 5, 5, 0xffffffff, 6);
					StructureCompassRenderUtils.drawConfiguredStringOnHUD(guiGraphics, String.valueOf(stack.getOrDefault(ModDataComponents.SEARCH_RADIUS, 0)), 5, 5, 0xffaaaaaa, 7);
				}
			}
		}
	}

}