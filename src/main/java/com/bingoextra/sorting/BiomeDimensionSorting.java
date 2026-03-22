package com.bingoextra.sorting;

import com.bingoextra.BingoExtra;
import com.bingoextra.utils.BiomeUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class BiomeDimensionSorting implements BiomeISorting<String> {

	private static final Minecraft mc = Minecraft.getInstance();

	@Override
	public int compare(Identifier biomeId1, Identifier biomeId2) {
		return getValue(biomeId1).compareTo(getValue(biomeId2));
	}

	@Override
	public String getValue(Identifier biomeId) {
		if (mc.level != null) {
			return BiomeUtils.dimensionIdsToString(BingoExtra.dimensionsForAllowedBiomes.get(biomeId));
		}
		return "";
	}

	@Override
	public BiomeISorting<?> next() {
		return new BiomeXpLevelsSorting();
	}

	@Override
	public String getLocalizedName() {
		return I18n.get("string.bingoextra.dimension");
	}

}
