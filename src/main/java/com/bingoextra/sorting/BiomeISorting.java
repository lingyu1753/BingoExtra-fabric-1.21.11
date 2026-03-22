package com.bingoextra.sorting;

import java.util.Comparator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public interface BiomeISorting<T> extends Comparator<Identifier> {

	@Override
	public int compare(Identifier biomeId1, Identifier biomeId2);

	public T getValue(Identifier biomeId);

	public BiomeISorting<?> next();

	public String getLocalizedName();

}
