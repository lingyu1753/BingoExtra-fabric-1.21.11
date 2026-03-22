package com.bingoextra.sorting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;

import java.util.Comparator;

@Environment(EnvType.CLIENT)
public interface StructureISorting extends Comparator<Identifier> {

	@Override
	public int compare(Identifier id1, Identifier id2);

	public Object getValue(Identifier id);

	public StructureISorting next();

	public String getLocalizedName();

}