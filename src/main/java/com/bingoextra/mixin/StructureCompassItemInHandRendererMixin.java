package com.bingoextra.mixin;

import com.bingoextra.item.StructureCompassItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ItemInHandRenderer.class)
public class StructureCompassItemInHandRendererMixin {

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
		if (newMainStack.getItem() instanceof StructureCompassItem && mainHandItem.getItem() instanceof StructureCompassItem) {
			StructureCompassItem newMainCompass = (StructureCompassItem) newMainStack.getItem();
			StructureCompassItem mainCompass = (StructureCompassItem) mainHandItem.getItem();
			if (newMainCompass.getCompassState(newMainStack) == mainCompass.getCompassState(mainHandItem)) {
				mainHandItem = newMainStack;
			}
		}

		ItemStack newOffStack = minecraft.player.getOffhandItem();
		if (newOffStack.getItem() instanceof StructureCompassItem && offHandItem.getItem() instanceof StructureCompassItem) {
			StructureCompassItem newOffCompass = (StructureCompassItem) newOffStack.getItem();
			StructureCompassItem offCompass = (StructureCompassItem) offHandItem.getItem();
			if (newOffCompass.getCompassState(newOffStack) == offCompass.getCompassState(offHandItem)) {
				offHandItem = newOffStack;
			}
		}
	}

}