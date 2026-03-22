package com.bingoextra.item.equipment;

import com.google.common.collect.Maps;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

public interface ModArmorMaterials {
    ArmorMaterial SNEAKERS = new ArmorMaterial(
            10, makeDefense(2, 5, 6, 2, 5), 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ItemTags.REPAIRS_IRON_ARMOR, EquipmentAssets.IRON
    );

    private static Map<ArmorType, Integer> makeDefense(int i, int j, int k, int l, int m) {
        return Maps.newEnumMap(Map.of(ArmorType.BOOTS, i, ArmorType.LEGGINGS, j, ArmorType.CHESTPLATE, k, ArmorType.HELMET, l, ArmorType.BODY, m));
    }
}
