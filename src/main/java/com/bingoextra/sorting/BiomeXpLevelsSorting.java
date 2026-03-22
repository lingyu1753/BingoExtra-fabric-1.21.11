package com.bingoextra.sorting;

import com.bingoextra.BingoExtra;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;

public class BiomeXpLevelsSorting implements BiomeISorting<String> {

	@Override
	public int compare(Identifier biomeId1, Identifier biomeId2) {
		return getValue(biomeId1).compareTo(getValue(biomeId2));
	}

	@Override
	public String getValue(Identifier biomeId) {
		if (BingoExtra.xpLevelsForAllowedBiomes.containsKey(biomeId)) {
			return String.valueOf(BingoExtra.xpLevelsForAllowedBiomes.get(biomeId));
		}
		return "0";
	}

	@Override
	public BiomeISorting<?> next() {
		return new BiomeRainfallSorting();
	}

	@Override
	public String getLocalizedName() {
		return I18n.get("string.bingoextra.levels");
	}

}
