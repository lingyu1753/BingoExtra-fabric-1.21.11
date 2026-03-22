package com.bingoextra.sorting;

import com.bingoextra.utils.StructureUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class StructureSourceSorting implements StructureISorting {
	
	@Override
	public int compare(Identifier id1, Identifier id2) {
		return StructureUtils.getStructureSource(id1).compareTo(StructureUtils.getStructureSource(id2));
	}

	@Override
	public Object getValue(Identifier id) {
		return StructureUtils.getStructureSource(id);
	}

	@Override
	public StructureISorting next() {
		return new StructureDimensionSorting();
	}

	@Override
	public String getLocalizedName() {
		return I18n.get("string.bingoextra.source");
	}

}
