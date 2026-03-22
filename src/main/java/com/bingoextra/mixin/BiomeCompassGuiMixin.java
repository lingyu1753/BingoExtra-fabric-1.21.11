package com.bingoextra.mixin;

import com.bingoextra.config.BiomeCompassConfig;
import com.bingoextra.core.component.ModDataComponents;
import com.bingoextra.utils.BiomeItemUtils;
import com.bingoextra.utils.BiomeRenderUtils;
import com.bingoextra.utils.BiomeUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bingoextra.item.BiomeCompassItem;
import com.bingoextra.utils.BiomeCompassState;

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

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public class BiomeCompassGuiMixin {
	
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V", at = @At(value = "TAIL"))
	private void renderCompassInfo(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
		if (minecraft.player != null && minecraft.level != null && !minecraft.options.hideGui && !minecraft.getDebugOverlay().showDebugScreen() && (minecraft.screen == null || (BiomeCompassConfig.displayWithChatOpen && minecraft.screen instanceof ChatScreen))) {
			final Player player = minecraft.player;
			final ItemStack stack = BiomeItemUtils.getHeldNatureCompass(player);
			if (stack != null && stack.getItem() instanceof BiomeCompassItem) {
				final BiomeCompassItem compass = (BiomeCompassItem) stack.getItem();
				if (compass.getCompassState(stack) == BiomeCompassState.SEARCHING) {
					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.status"), 5, 5, 0xffffffff, 0);
					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.searching"), 5, 5, 0xffaaaaaa, 1);

					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.biome"), 5, 5, 0xffffffff, 3);
					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, BiomeUtils.getBiomeName(minecraft.level, Identifier.parse(stack.getOrDefault(ModDataComponents.BIOME_ID, ""))), 5, 5, 0xffaaaaaa, 4);

					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.radius"), 5, 5, 0xffffffff, 6);
 					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, String.valueOf(stack.getOrDefault(ModDataComponents.SEARCH_RADIUS, 0)), 5, 5, 0xffaaaaaa, 7);
				} else if (compass.getCompassState(stack) == BiomeCompassState.FOUND) {
					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.status"), 5, 5, 0xffffffff, 0);
					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.found"), 5, 5, 0xffaaaaaa, 1);

					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.biome"), 5, 5, 0xffffffff, 3);
					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, BiomeUtils.getBiomeName(minecraft.level, Identifier.parse(stack.getOrDefault(ModDataComponents.BIOME_ID, ""))), 5, 5, 0xffaaaaaa, 4);

					if (stack.getOrDefault(ModDataComponents.DISPLAY_COORDS, false)) {
						BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.coordinates"), 5, 5, 0xffffffff, 6);
						BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, stack.getOrDefault(ModDataComponents.FOUND_X, 0) + ", " + stack.getOrDefault(ModDataComponents.FOUND_Z, 0), 5, 5, 0xffaaaaaa, 7);

						BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.distance"), 5, 5, 0xffffffff, 9);
						BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, String.valueOf(BiomeUtils.getDistanceToBiome(player, stack.getOrDefault(ModDataComponents.FOUND_X, 0), stack.getOrDefault(ModDataComponents.FOUND_Z, 0))), 5, 5, 0xffaaaaaa, 10);
					}
				} else if (compass.getCompassState(stack) == BiomeCompassState.NOT_FOUND) {
					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.status"), 5, 5, 0xffffffff, 0);
					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.notFound"), 5, 5, 0xffaaaaaa, 1);

					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.biome"), 5, 5, 0xffffffff, 3);
					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, BiomeUtils.getBiomeName(minecraft.level, Identifier.parse(stack.getOrDefault(ModDataComponents.BIOME_ID, ""))), 5, 5, 0xffaaaaaa, 4);

					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.radius"), 5, 5, 0xffffffff, 6);
					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, String.valueOf(stack.getOrDefault(ModDataComponents.SEARCH_RADIUS, 0)), 5, 5, 0xffaaaaaa, 7);

					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, I18n.get("string.bingoextra.samples"), 5, 5, 0xffffffff, 9);
					BiomeRenderUtils.drawConfiguredStringOnHUD(guiGraphics, String.valueOf(stack.getOrDefault(ModDataComponents.SAMPLES, 0)), 5, 5, 0xffaaaaaa, 10);
				}
			}
		}
	}
	
}