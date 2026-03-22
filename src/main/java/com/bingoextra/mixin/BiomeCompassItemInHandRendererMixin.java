package com.bingoextra.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bingoextra.item.BiomeCompassItem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(ItemInHandRenderer.class)
public class BiomeCompassItemInHandRendererMixin {

	@Shadow
	private ItemStack mainHandItem;

	@Shadow
	private ItemStack offHandItem;

	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "tick()V", at = @At("HEAD"))
	private void cancelCompassAnimation(CallbackInfo ci) {
		ItemStack newMainStack = minecraft.player.getMainHandItem();
		if (newMainStack.getItem() instanceof BiomeCompassItem && mainHandItem.getItem() instanceof BiomeCompassItem) {
			BiomeCompassItem newMainCompass = (BiomeCompassItem) newMainStack.getItem();
			BiomeCompassItem mainCompass = (BiomeCompassItem) mainHandItem.getItem();
			if (newMainCompass.getCompassState(newMainStack) == mainCompass.getCompassState(mainHandItem)) {
				mainHandItem = newMainStack;
			}
		}

		ItemStack newOffStack = minecraft.player.getOffhandItem();
		if (newOffStack.getItem() instanceof BiomeCompassItem && offHandItem.getItem() instanceof BiomeCompassItem) {
			BiomeCompassItem newOffCompass = (BiomeCompassItem) newOffStack.getItem();
			BiomeCompassItem offCompass = (BiomeCompassItem) offHandItem.getItem();
			if (newOffCompass.getCompassState(newOffStack) == offCompass.getCompassState(offHandItem)) {
				offHandItem = newOffStack;
			}
		}
	}

}