package com.bingoextra.utils;

import com.mojang.serialization.MapCodec;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BiomeCompassAngle implements RangeSelectItemModelProperty {

	public static final MapCodec<BiomeCompassAngle> MAP_CODEC = MapCodec.unit(new BiomeCompassAngle());
	private final BiomeCompassAngleState state;

	public BiomeCompassAngle() {
		this(new BiomeCompassAngleState());
	}

	private BiomeCompassAngle(BiomeCompassAngleState state) {
		this.state = state;
	}
	
	@Override
	public float get(ItemStack stack, ClientLevel level, ItemOwner owner, int seed) {
		return state.get(stack, level, owner, seed);
	}

	@Override
	public MapCodec<BiomeCompassAngle> type() {
		return MAP_CODEC;
	}

}