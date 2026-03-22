package com.bingoextra.utils;

import com.bingoextra.BingoExtra;

import com.bingoextra.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BiomeItemUtils {
	
	public static boolean isCompass(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() == ModItems.BIOME_COMPASS;
	}

	public static ItemStack getHeldNatureCompass(Player player) {
		return getHeldItem(player, ModItems.BIOME_COMPASS);
	}

	public static ItemStack getHeldItem(Player player, Item item) {
		if (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() == item) {
			return player.getMainHandItem();
		} else if (!player.getOffhandItem().isEmpty() && player.getOffhandItem().getItem() == item) {
			return player.getOffhandItem();
		}

		return ItemStack.EMPTY;
	}

}
