package com.bingoextra.utils;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class StructureCompassAngle implements RangeSelectItemModelProperty {
	
	public static final MapCodec<StructureCompassAngle> MAP_CODEC = MapCodec.unit(new StructureCompassAngle());
	private final StructureCompassAngleState state;
	
	public StructureCompassAngle() {
		this(new StructureCompassAngleState());
	}
	
	private StructureCompassAngle(StructureCompassAngleState state) {
		this.state = state;
	}
	
	@Override
	public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
		return state.get(stack, level, owner, seed);
	}
	
	@Override
	public MapCodec<StructureCompassAngle> type() {
		return MAP_CODEC;
	}
	
}