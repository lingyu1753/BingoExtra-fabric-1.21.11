package com.bingoextra.mixin;

import com.bingoextra.utils.BiomeCompassAngle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bingoextra.BingoExtra;
import com.mojang.serialization.MapCodec;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

@Environment(EnvType.CLIENT)
@Mixin(RangeSelectItemModelProperties.class)
public class BiomeCompassRangeSelectItemModelPropertiesMixin {
	
	@Shadow
	@Final
	public static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends RangeSelectItemModelProperty>> ID_MAPPER;

	@Inject(method = "bootstrap()V", at = @At(value = "TAIL"))
	private static void registerCompassProperty(CallbackInfo info) {
		ID_MAPPER.put(Identifier.fromNamespaceAndPath(BingoExtra.MOD_ID, "biome_angle"), BiomeCompassAngle.MAP_CODEC);
	}
	
}