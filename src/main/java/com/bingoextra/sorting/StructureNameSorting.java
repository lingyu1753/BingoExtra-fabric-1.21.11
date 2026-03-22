package com.bingoextra.sorting;

import com.bingoextra.utils.StructureUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class StructureNameSorting implements StructureISorting {
	
	@Override
	public int compare(Identifier id1, Identifier id2) {
		return StructureUtils.getStructureName(id1).compareTo(StructureUtils.getStructureName(id2));
	}

	@Override
	public Object getValue(Identifier id) {
		return StructureUtils.getStructureName(id);
	}

	@Override
	public StructureISorting next() {
		return new StructureSourceSorting();
	}

	@Override
	public String getLocalizedName() {
		return I18n.get("string.bingoextra.name");
	}

}
