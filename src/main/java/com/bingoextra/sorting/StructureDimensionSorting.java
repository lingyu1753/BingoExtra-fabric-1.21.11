package com.bingoextra.sorting;

import com.bingoextra.BingoExtra;
import com.bingoextra.utils.StructureUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class StructureDimensionSorting implements StructureISorting {
	
	@Override
	public int compare(Identifier id1, Identifier id2) {
		return StructureUtils.structureDimensionsToString(BingoExtra.dimensionsForAllowedStructures.get(id1)).compareTo(StructureUtils.structureDimensionsToString(BingoExtra.dimensionsForAllowedStructures.get(id2)));
	}

	@Override
	public Object getValue(Identifier id) {
		return StructureUtils.structureDimensionsToString(BingoExtra.dimensionsForAllowedStructures.get(id));
	}

	@Override
	public StructureISorting next() {
		return new StructureGroupSorting();
	}

	@Override
	public String getLocalizedName() {
		return I18n.get("string.bingoextra.dimension");
	}

}
